/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlcurran.messages.telephony;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.telephony.SmsManager;

import com.amlcurran.messages.AndroidMessageTransport;
import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.ParcelablePhoneNumber;

import java.util.ArrayList;

public class SmsManagerOutputPort extends IntentService {

    public static final String TAG = SmsManagerOutputPort.class.getSimpleName();

    public static final String ACTION_SEND_REQUEST = "send_request";
    private static final String ACTION_RESEND = "resent";
    static final String EXTRA_MESSAGE = "message";
    public static final String FROM_WEAR = "wear";
    public static final String EXTRA_NUMBER = "number";
    public static final String EXTRA_VOICE_REPLY = "voice_reply";
    private static final String EXTRA_THREAD_ID = "thread_id";

    private final MessageRepository messageRepository;
    private final SmsManager smsManager;

    public SmsManagerOutputPort() {
        super(TAG);
        setIntentRedelivery(true);
        SmsDatabaseWriter smsDatabaseWriter = new SmsDatabaseWriter(this);
        messageRepository = new MessageRepository(smsDatabaseWriter);
        smsManager = SmsManager.getDefault();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MessagesLog.d(this, intent.toString());
        if (isSendRequest(intent)) {

            String threadId = intent.getStringExtra(EXTRA_THREAD_ID);
            InFlightSmsMessage message;
            if (isFromWear(intent)) {
                message = extractInFlightFromWear(intent);
            } else {
                message = intent.getParcelableExtra(EXTRA_MESSAGE);
            }
            SmsMessage smsMessage = messageRepository.send(message, getContentResolver());
            sendBroadcast(AndroidMessageTransport.sendingMessageBroadcast(this, threadId, smsMessage));
            sendToApi(smsMessage);

        } else if (ACTION_RESEND.equals(intent.getAction())) {

            SingletonManager.getNotifier(this).clearFailureToSendNotification();
            SmsMessage message = (SmsMessage) intent.getSerializableExtra(EXTRA_MESSAGE);
            SmsMessage smsMessage = messageRepository.resend(message, getContentResolver());
            sendBroadcast(AndroidMessageTransport.sendingMessageBroadcast(this, message.getThreadId(), smsMessage));
            resendToApi(smsMessage);

        }
    }

    private void resendToApi(SmsMessage smsMessage) {
        ArrayList<PendingIntent> messageSendIntents = getMessageResendIntents(smsMessage);
        send(smsMessage, messageSendIntents);
    }

    ArrayList<PendingIntent> getMessageResendIntents(SmsMessage message) {
        ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
        pendingIntents.add(SmsManagerInputPort.InputReceiver.broadcastResent(this, message));
        return pendingIntents;
    }

    private void sendToApi(SmsMessage message) {
        ArrayList<PendingIntent> messageSendIntents = getMessageSendIntents(message);
        send(message, messageSendIntents);
    }

    ArrayList<PendingIntent> getMessageSendIntents(SmsMessage message) {
        ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
        pendingIntents.add(SmsManagerInputPort.InputReceiver.broadcast(this, message));
        return pendingIntents;
    }

    private void send(SmsMessage message, ArrayList<PendingIntent> messageSendIntents) {
        ArrayList<String> parts = smsManager.divideMessage(message.getBody());
        smsManager.sendMultipartTextMessage(message.getAddress().flatten(), null, parts, messageSendIntents, null);
    }

    private InFlightSmsMessage extractInFlightFromWear(Intent intent) {
        String address = intent.getStringExtra(EXTRA_NUMBER);
        CharSequence input = getMessageText(intent);
        return new InFlightSmsMessage(new ParcelablePhoneNumber(address), String.valueOf(input), Time.fromMillis(System.currentTimeMillis()));
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_VOICE_REPLY);
        }
        return null;
    }

    private static boolean isFromWear(Intent intent) {
        return intent.getBooleanExtra(FROM_WEAR, false);
    }

    private static boolean isSendRequest(Intent intent) {
        return ACTION_SEND_REQUEST.equals(intent.getAction());
    }

    public static Intent sendMessageIntent(Context context, String threadId, InFlightSmsMessage smsMessage) {
        Intent sendMessageIntent = new Intent(context, SmsManagerOutputPort.class);
        sendMessageIntent.setAction(SmsManagerOutputPort.ACTION_SEND_REQUEST);
        sendMessageIntent.putExtra(SmsManagerOutputPort.EXTRA_MESSAGE, smsMessage);
        sendMessageIntent.putExtra(SmsManagerOutputPort.EXTRA_THREAD_ID, threadId);
        return sendMessageIntent;
    }

    public static Intent resendMessageIntent(Context context, SmsMessage smsMessage) {
        Intent resendIntent = new Intent(context, SmsManagerOutputPort.class);
        resendIntent.setAction(SmsManagerOutputPort.ACTION_RESEND);
        resendIntent.putExtra(SmsManagerOutputPort.EXTRA_MESSAGE, smsMessage);
        return resendIntent;
    }

    public static PendingIntent resendPendingIntent(SmsMessage message, Context context) {
        return PendingIntent.getService(context, 0, resendMessageIntent(context, message), PendingIntent.FLAG_CANCEL_CURRENT);
    }
}

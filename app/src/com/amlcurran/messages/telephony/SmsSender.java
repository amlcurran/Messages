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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.telephony.SmsManager;

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.ParcelablePhoneNumber;
import com.amlcurran.messages.events.BroadcastEventBus;

public class SmsSender extends IntentService {

    public static final String TAG = SmsSender.class.getSimpleName();

    public static final String ACTION_SEND_REQUEST = "send_request";
    public static final String ACTION_MESSAGE_SENT = "message_send";
    public static final int IS_FROM_FAILURE = 1;
    static final String EXTRA_MESSAGE = "message";
    static final String EXTRA_OUTBOX_URI = "outbox_uri";
    private static final String EXTRA_FROM_FAILURE = "from_failure";
    public static final String FROM_WEAR = "wear";
    public static final String EXTRA_NUMBER = "number";
    public static final String EXTRA_VOICE_REPLY = "voice_reply";

    private final MessageRepository messageRepository;
    private final SmsSenderReal smsSenderReal;

    public SmsSender() {
        super(TAG);
        setIntentRedelivery(true);
        SmsDatabaseWriter smsDatabaseWriter = new SmsDatabaseWriter(this);
        EventBus eventBus = new BroadcastEventBus(this);
        messageRepository = new MessageRepository(smsDatabaseWriter, eventBus);
        smsSenderReal = new SmsSenderReal(this, SmsManager.getDefault());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MessagesLog.d(this, intent.toString());
        if (isSendRequest(intent)) {

            if (isFromFailure(intent)) {
                SingletonManager.getNotifier(this).clearFailureToSendNotification();
            }

            InFlightSmsMessage message;
            if (isFromWear(intent)) {
                message = extractInFlightFromWear(intent);
            } else {
                message = intent.getParcelableExtra(EXTRA_MESSAGE);
            }
            Uri inserted = messageRepository.send(message, getContentResolver());
            smsSenderReal.send(message, inserted);

        } else if (isSentNotification(intent)) {
            InFlightSmsMessage message = intent.getParcelableExtra(EXTRA_MESSAGE);
            Uri outboxSms = Uri.parse(intent.getStringExtra(EXTRA_OUTBOX_URI));
            if (messageRepository.successfullySent(intent)) {
                messageRepository.sent(message, outboxSms);
            } else {
                notifyFailureToSend(message);
                messageRepository.failedToSend(message, outboxSms);
            }
        }
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

    private boolean isFromWear(Intent intent) {
        return intent.getBooleanExtra(FROM_WEAR, false);
    }

    private boolean isFromFailure(Intent intent) {
        return intent.getIntExtra(EXTRA_FROM_FAILURE, -1) == IS_FROM_FAILURE;
    }

    private void notifyFailureToSend(InFlightSmsMessage message) {
        SingletonManager.getNotifier(this).showSendError(message);
    }

    private boolean isSentNotification(Intent intent) {
        return ACTION_MESSAGE_SENT.equals(intent.getAction());
    }

    private boolean isSendRequest(Intent intent) {
        return ACTION_SEND_REQUEST.equals(intent.getAction());
    }

    public static Intent sendMessageIntent(Context context, InFlightSmsMessage smsMessage) {
        Intent sendMessageIntent = new Intent(context, SmsSender.class);
        sendMessageIntent.setAction(SmsSender.ACTION_SEND_REQUEST);
        sendMessageIntent.putExtra(SmsSender.EXTRA_MESSAGE, smsMessage);
        return sendMessageIntent;
    }

    public static Intent resendMessageIntent(Context context, InFlightSmsMessage smsMessage) {
        Intent resendMessageIntent = sendMessageIntent(context, smsMessage);
        resendMessageIntent.putExtra(SmsSender.EXTRA_FROM_FAILURE, SmsSender.IS_FROM_FAILURE);
        return resendMessageIntent;
    }
}

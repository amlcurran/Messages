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

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.notifications.Notifier;

import java.util.ArrayList;

public class SmsSender extends IntentService {

    public static final String TAG = SmsSender.class.getSimpleName();

    public static final String EXTRA_MESSAGE = "message";
    public static final String ACTION_SEND_REQUEST = "send_request";
    public static final String ACTION_MESSAGE_SENT = "message_send";
    public static final int IS_FROM_FAILURE = 1;
    private static final String EXTRA_OUTBOX_URI = "outbox_uri";
    public static final String EXTRA_FROM_FAILURE = "from_failure";

    private final SmsManager smsManager;
    private final SmsDatabaseWriter smsDatabaseWriter;
    private final BroadcastEventBus eventBus;

    public SmsSender() {
        super(TAG);
        smsManager = SmsManager.getDefault();
        smsDatabaseWriter = new SmsDatabaseWriter();
        eventBus = new BroadcastEventBus(this);
        setIntentRedelivery(true);
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
                new Notifier(this).clearFailureToSendNotification();
            }

            InFlightSmsMessage message = intent.getParcelableExtra(EXTRA_MESSAGE);
            sendMessage(message);

        } else if (isSentNotification(intent)) {
            int result = intent.getIntExtra(SmsReceiver.EXTRA_RESULT, 0);
            InFlightSmsMessage message = intent.getParcelableExtra(EXTRA_MESSAGE);
            Uri outboxSms = Uri.parse(intent.getStringExtra(EXTRA_OUTBOX_URI));
            if (result == Activity.RESULT_OK) {
                deleteOutboxMessages(message.getAddress());
                writeMessageToProvider(message);
            } else {
                notifyFailureToSend(message, result);
            }
        }
    }

    private boolean isFromFailure(Intent intent) {
        return intent.getIntExtra(EXTRA_FROM_FAILURE, -1) == IS_FROM_FAILURE;
    }

    private void deleteOutboxMessages(String address) {
        smsDatabaseWriter.deleteOutboxMessages(getContentResolver(), address);
    }

    private void notifyFailureToSend(InFlightSmsMessage message, int result) {
        SingletonManager.getNotifier(this).showSendError(message);
    }

    private void writeMessageToProvider(InFlightSmsMessage message) {
        smsDatabaseWriter.writeSentMessage(getContentResolver(), new SmsDatabaseWriter.WriteListener() {

            @Override
            public void written(Uri inserted) {
                new BroadcastEventBus(SmsSender.this).postMessageSent();
            }

            @Override
            public void failed() {
                MessagesLog.e(SmsSender.this, "Failed to write a sent message to the database");
            }
        }, message);
    }

    private void sendMessage(final InFlightSmsMessage message) {
        smsDatabaseWriter.writeOutboxSms(getContentResolver(), new SmsDatabaseWriter.WriteListener() {

            @Override
            public void written(Uri inserted) {
                eventBus.postMessageSending(message);
                ArrayList<PendingIntent> messageSendIntents = getMessageSendIntents(message, inserted);
                smsManager.sendMultipartTextMessage(message.getAddress(), null, smsManager.divideMessage(message.getBody()), messageSendIntents, null);
            }

            @Override
            public void failed() {

            }
        }, message);
    }

    private ArrayList<PendingIntent> getMessageSendIntents(InFlightSmsMessage message, Uri inserted) {
        Intent intent = new Intent(this, SmsReceiver.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_OUTBOX_URI, inserted.toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>();
        pendingIntents.add(pendingIntent);
        return pendingIntents;
    }

    private boolean isSentNotification(Intent intent) {
        return intent.getAction().equals(ACTION_MESSAGE_SENT);
    }

    private boolean isSendRequest(Intent intent) {
        return intent.getAction().equals(ACTION_SEND_REQUEST);
    }
}

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

package com.amlcurran.messages;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;

public class SmsSender extends IntentService implements SmsDatabaseWriter.SentWriteListener {

    public static final String TAG = SmsSender.class.getSimpleName();

    public static final String EXTRA_MESSAGE = "message";
    public static final String ACTION_SEND_REQUEST = "send_request";
    public static final String ACTION_MESSAGE_SENT = "message_send";
    public static final String BROADCAST_MESSAGE_SENT = "broadcast_message_sent";

    private final SmsManager smsManager;
    private final SmsDatabaseWriter smsDatabaseWriter;

    public SmsSender() {
        super(TAG);
        smsManager = SmsManager.getDefault();
        smsDatabaseWriter = new SmsDatabaseWriter();
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, intent.toString());
        if (isSendRequest(intent)) {
            SmsMessage message = intent.getParcelableExtra(EXTRA_MESSAGE);
            sendMessage(message);
        } else if (isSentNotification(intent)) {
            SmsMessage message = intent.getParcelableExtra(EXTRA_MESSAGE);
            writeMessageToProvider(message);
        }
    }

    private void writeMessageToProvider(SmsMessage message) {
        smsDatabaseWriter.writeSentMessage(getContentResolver(), this, message);
        Log.d(TAG, "Write sent message to provider " + message.toString());
    }

    private void sendLocalBroadcast() {
        Intent sentIntent = new Intent(BROADCAST_MESSAGE_SENT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(sentIntent);
    }

    private void sendMessage(SmsMessage message) {
        Log.d(TAG, "Sending message: " + message.toString());
        ArrayList<PendingIntent> messageSendIntents = getMessageSendIntents(message);
        smsManager.sendMultipartTextMessage(message.getAddress(), null, smsManager.divideMessage(message.getBody()), messageSendIntents, null);
    }

    private ArrayList<PendingIntent> getMessageSendIntents(SmsMessage message) {
        Intent intent = new Intent(ACTION_MESSAGE_SENT);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.setClass(this, SmsSender.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, message.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
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

    @Override
    public void onWrittenToSentBox() {
        Log.d(TAG, "Sending broadcast for sent message");
        sendLocalBroadcast();
    }

    @Override
    public void onSentBoxWriteFailed() {
        Log.e(TAG, "Failed to write a sent message to the database");
    }
}

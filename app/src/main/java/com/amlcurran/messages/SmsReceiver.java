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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver implements SmsDatabaseWriter.InboxWriteListener {

    public static final String TAG = SmsReceiver.class.getSimpleName();
    public static final String BROADCAST_MESSAGE_RECEIVED = "broadcast_message_received";

    private final SmsDatabaseWriter smsDatabaseWriter;

    public SmsReceiver() {
        smsDatabaseWriter = new SmsDatabaseWriter();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        android.telephony.SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        writeSmsToProvider(context, com.amlcurran.messages.SmsMessage.fromDeliverBroadcast(messages));
    }

    private void sendLocalBroadcast(Context context) {
        Intent sentIntent = new Intent(BROADCAST_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(sentIntent);
    }

    private void writeSmsToProvider(final Context context, final SmsMessage message) {
        Log.d(TAG, "Writing received SMS to provider: " + message.toString());

        smsDatabaseWriter.writeInboxSms(context.getContentResolver(), new SmsDatabaseWriter.InboxWriteListener() {
            @Override
            public void onWrittenToInbox() {
                Log.d(TAG, "Sending broadcast of message received");
                sendLocalBroadcast(context);
                MessagesApp.getNotifier(context).updateUnreadNotification();
            }

            @Override
            public void onInboxWriteFailed() {
                Log.e(TAG, "Failed to write message to inbox database");
            }
        }, message);

    }

    @Override
    public void onWrittenToInbox() {
    }

    @Override
    public void onInboxWriteFailed() {

    }
}

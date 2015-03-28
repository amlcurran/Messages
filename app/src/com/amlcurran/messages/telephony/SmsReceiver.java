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
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.InFlightSmsMessageFactory;

public class SmsReceiver extends BroadcastReceiver {

    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_OUTBOX_URI = "outbox_uri";
    private static final String EXTRA_MESSAGE_ID = "message_id";

    static PendingIntent broadcast(Context context, InFlightSmsMessage message, long messageId) {
        Intent intent = new Intent(context, SmsReceiver.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_MESSAGE_ID, messageId);
        return PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_DELIVER_ACTION.equals(intent.getAction())) {

            android.telephony.SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            InFlightSmsMessage inFlightSmsMessage = InFlightSmsMessageFactory.fromDeliverBroadcast(messages);
            Intent asyncWriteIntent = SmsAsyncService.getAsyncWriteIntent(context, inFlightSmsMessage, WriteType.INBOX);
            context.startService(asyncWriteIntent);

        } else {

            InFlightSmsMessage message = intent.getParcelableExtra(EXTRA_MESSAGE);
            long messageId = intent.getLongExtra(EXTRA_MESSAGE_ID, -1);
            if (sentSuccessfully()) {
                context.startService(SmsSentNotificationService.sentIntent(context, message, messageId));
            } else {
                context.startService(SmsSentNotificationService.failedSentIntent(context, message, messageId));
            }

        }
    }

    private boolean sentSuccessfully() {
        return getResultCode() == Activity.RESULT_OK;
    }

}

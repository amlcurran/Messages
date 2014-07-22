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

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.events.BroadcastEventBus;

public class SmsAsyncService extends IntentService {

    public SmsAsyncService() {
        super(SmsAsyncService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (SmsReceiver.ASYNC_WRITE.equals(intent.getAction())) {

            WriteType writeType = WriteType.fromIntent(intent);
            InFlightSmsMessage smsMessage = intent.getParcelableExtra(SmsReceiver.EXTRA_MESSAGE);

            switch (writeType) {

                case INBOX:
                    writeInbox(smsMessage);
                    break;

                case DRAFT:
                    writeDraft(smsMessage);
                    break;

            }
        }
    }

    private void writeDraft(final InFlightSmsMessage smsMessage) {
        SmsDatabaseWriter smsDatabaseWriter = new SmsDatabaseWriter();
        smsDatabaseWriter.writeDraft(smsMessage, getContentResolver(), new SmsDatabaseWriter.WriteListener() {

            @Override
            public void written(Uri inserted) {
                new BroadcastEventBus(SmsAsyncService.this).postMessageDrafted(smsMessage.getPhoneNumber());
                MessagesLog.d(SmsAsyncService.this, "Written Draft");
            }

            @Override
            public void failed() {
                MessagesLog.e(SmsAsyncService.this, "Failed to write message to inbox database");
            }

        });
    }

    private void writeInbox(final InFlightSmsMessage smsMessage) {
        SmsDatabaseWriter smsDatabaseWriter = new SmsDatabaseWriter();
        smsDatabaseWriter.writeInboxSms(getContentResolver(), new SmsDatabaseWriter.WriteListener() {

            @Override
            public void written(Uri inserted) {
                new BroadcastEventBus(SmsAsyncService.this).postMessageReceived(smsMessage.getPhoneNumber());
                SingletonManager.getNotifier(SmsAsyncService.this).updateUnreadNotification(true);
            }

            @Override
            public void failed() {
                MessagesLog.e(SmsAsyncService.this, "Failed to write message to inbox database");
            }

        }, smsMessage);
    }

    public static Intent getAsyncWriteIntent(Context context, InFlightSmsMessage message, WriteType writeType) {
        Intent intent = new Intent(context, SmsAsyncService.class);
        intent.setAction(SmsReceiver.ASYNC_WRITE);
        intent.putExtra(SmsReceiver.EXTRA_MESSAGE, message);
        intent.putExtra(SmsReceiver.EXTRA_WRITE_TYPE, writeType.toString());
        return intent;
    }
}

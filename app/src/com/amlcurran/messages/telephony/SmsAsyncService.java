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
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Telephony;

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.events.BroadcastEventBus;

public class SmsAsyncService extends IntentService {

    static final String ASYNC_WRITE = "com.amlcurran.messages.smsreceiver.ASYNC_WRITE";
    private static final String ACTION_DELETE = SmsAsyncService.class.getCanonicalName() + ".ACTION_DELETE";
    private static final String EXTRA_MESSAGE_ID = "message_id";
    static final String EXTRA_MESSAGE = "message";
    static final String EXTRA_WRITE_TYPE = "write_type";

    public SmsAsyncService() {
        super(SmsAsyncService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ASYNC_WRITE.equals(intent.getAction())) {

            WriteType writeType = WriteType.valueOf(intent.getStringExtra(EXTRA_WRITE_TYPE));
            InFlightSmsMessage smsMessage = intent.getParcelableExtra(EXTRA_MESSAGE);

            switch (writeType) {

                case INBOX:
                    writeInbox(smsMessage);
                    break;

                case DRAFT:
                    writeDraft(smsMessage);
                    break;

            }
        } else if (ACTION_DELETE.equals(intent.getAction())) {

            long id = intent.getLongExtra(EXTRA_MESSAGE_ID, -1);
            if (id > 0) {
                Uri deleteUri = ContentUris.withAppendedId(Telephony.Sms.CONTENT_URI, id);
                int deleted = new SmsDatabaseWriter(this).deleteFromUri(getContentResolver(), deleteUri);
                if (deleted > 0) {
                    MessagesLog.d(this, "Successfully deleted message");
                }
            }

        }
    }

    private void writeDraft(final InFlightSmsMessage smsMessage) {
        SmsDatabaseWriter smsDatabaseWriter = new SmsDatabaseWriter(this);
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
        SmsDatabaseWriter smsDatabaseWriter = new SmsDatabaseWriter(this);
        SmsMessage message = smsDatabaseWriter.writeInboxSms(getContentResolver(), smsMessage);
        SingletonManager.getMessageTransport(this).received(message);
    }

    public static Intent getAsyncWriteIntent(Context context, InFlightSmsMessage message, WriteType writeType) {
        Intent intent = new Intent(context, SmsAsyncService.class);
        intent.setAction(ASYNC_WRITE);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_WRITE_TYPE, writeType.toString());
        return intent;
    }

    public static Intent getAsyncDeleteIntent(Context context, SmsMessage message) {
        Intent intent = new Intent(context, SmsAsyncService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(EXTRA_MESSAGE_ID, message.getId());
        return intent;
    }

}

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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Telephony;

import com.amlcurran.messages.data.InFlightSmsMessageFactory;
import com.amlcurran.messages.data.InFlightSmsMessage;

public class SmsDatabaseWriter {

    public void writeSentMessage(ContentResolver contentResolver, WriteListener sentWriteListener, InFlightSmsMessage message) {
        final ContentValues values = InFlightSmsMessageFactory.toContentValues(message, Telephony.Sms.MESSAGE_TYPE_SENT);
        values.put(Telephony.Sms.Sent.READ, "1");
        writeSentSmsInternal(contentResolver, sentWriteListener, values);
    }

    private static void writeSentSmsInternal(ContentResolver contentResolver, WriteListener sentWriteListener, ContentValues values) {
        Uri uri = contentResolver.insert(Telephony.Sms.Sent.CONTENT_URI, values);
        if (uri != null) {
            sentWriteListener.written(uri);
        } else {
            sentWriteListener.failed();
        }
    }

    public void writeInboxSms(ContentResolver resolver, WriteListener inboxWriteListener, InFlightSmsMessage message) {
        ContentValues contentValues = InFlightSmsMessageFactory.toContentValues(message, Telephony.Sms.Sent.MESSAGE_TYPE_INBOX);
        writeInboxSmsInternal(resolver, inboxWriteListener, contentValues);
    }

    private static void writeInboxSmsInternal(ContentResolver resolver, WriteListener inboxWriteListener, ContentValues contentValues) {
        Uri inserted = resolver.insert(Telephony.Sms.Inbox.CONTENT_URI, contentValues);
        if (inserted != null) {
            inboxWriteListener.written(inserted);
        } else {
            inboxWriteListener.failed();
        }
    }

    public void writeOutboxSms(ContentResolver contentResolver, WriteListener outboxWriteListener, InFlightSmsMessage message) {
        ContentValues contentValues = InFlightSmsMessageFactory.toContentValues(message, Telephony.Sms.Sent.MESSAGE_TYPE_OUTBOX);
        writeOutboxSmsInternal(contentResolver, outboxWriteListener, contentValues);
    }

    private static void writeOutboxSmsInternal(ContentResolver resolver, WriteListener writeListener, ContentValues contentValues) {
        Uri inserted = resolver.insert(Telephony.Sms.Inbox.CONTENT_URI, contentValues);
        if (inserted != null) {
            writeListener.written(inserted);
        } else {
            writeListener.failed();
        }
    }

    public void deleteOutboxMessages(ContentResolver contentResolver, String outboundAddress) {
        String selection = String.format("%1$s=? AND %2$s=?", Telephony.Sms.ADDRESS, Telephony.Sms.TYPE);
        String[] args = new String[] { outboundAddress, String.valueOf(Telephony.Sms.MESSAGE_TYPE_OUTBOX) };
        contentResolver.delete(Telephony.Sms.CONTENT_URI, selection, args);
    }

    public void writeDraft(InFlightSmsMessage smsMessage, ContentResolver contentResolver, WriteListener writeListener) {
        ContentValues contentValues = InFlightSmsMessageFactory.toContentValues(smsMessage, Telephony.Sms.MESSAGE_TYPE_DRAFT);
        Uri uri = contentResolver.insert(Telephony.Sms.Draft.CONTENT_URI, contentValues);
        if (uri != null) {
            writeListener.written(uri);
        } else {
            writeListener.failed();
        }
    }

    public interface WriteListener {
        void written(Uri inserted);
        void failed();
    }


}

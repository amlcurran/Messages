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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Telephony;

public class SmsDatabaseWriter {

    public void writeSentMessage(final ContentResolver contentResolver,
                                 final SentWriteListener sentWriteListener, SmsMessage message) {
        final ContentValues values = message.toContentValues(Telephony.Sms.MESSAGE_TYPE_SENT);
        values.put(Telephony.Sms.Sent.READ, "1");
        writeSentSmsInternal(contentResolver, sentWriteListener, values);
    }

    private static void writeSentSmsInternal(ContentResolver contentResolver, SentWriteListener sentWriteListener, ContentValues values) {
        Uri uri = contentResolver.insert(Telephony.Sms.Sent.CONTENT_URI, values);
        if (uri != null) {
            sentWriteListener.onWrittenToSentBox();
        } else {
            sentWriteListener.onSentBoxWriteFailed();
        }
    }

    public void writeInboxSms(final ContentResolver resolver, final InboxWriteListener inboxWriteListener, SmsMessage message) {
        final ContentValues contentValues = message.toContentValues(Telephony.Sms.Sent.MESSAGE_TYPE_INBOX);
        writeInboxSmsInternal(resolver, inboxWriteListener, contentValues);
    }

    private static void writeInboxSmsInternal(ContentResolver resolver, InboxWriteListener inboxWriteListener, ContentValues contentValues) {
        Uri inserted = resolver.insert(Telephony.Sms.Inbox.CONTENT_URI, contentValues);
        if (inserted != null) {
            inboxWriteListener.onWrittenToInbox();
        } else {
            inboxWriteListener.onInboxWriteFailed();
        }
    }

    public interface InboxWriteListener {
        void onWrittenToInbox();

        void onInboxWriteFailed();
    }

    public interface SentWriteListener {
        void onWrittenToSentBox();

        void onSentBoxWriteFailed();
    }

}

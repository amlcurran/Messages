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

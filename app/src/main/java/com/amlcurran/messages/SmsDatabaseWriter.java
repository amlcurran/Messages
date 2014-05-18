package com.amlcurran.messages;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Telephony;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmsDatabaseWriter {

    private final ExecutorService executorService;

    public SmsDatabaseWriter() {
        executorService = Executors.newSingleThreadExecutor();
    }

    private static ContentValues valuesFromMessage(SmsMessage message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Telephony.Sms.Inbox.BODY, message.getBody());
        contentValues.put(Telephony.Sms.Inbox.ADDRESS, message.getAddress());
        contentValues.put(Telephony.Sms.Inbox.DATE, message.getTimestamp());
        contentValues.put(Telephony.Sms.Inbox.DATE_SENT, message.getTimestamp());
        contentValues.put(Telephony.Sms.Inbox.TYPE, Telephony.Sms.Inbox.MESSAGE_TYPE_INBOX);
        return contentValues;
    }

    public void writeSentMessage(final ContentResolver contentResolver,
                                 final SentWriteListener sentWriteListener, SmsMessage message) {

        final ContentValues values = message.toContentValues(Telephony.Sms.MESSAGE_TYPE_SENT);
        values.put(Telephony.Sms.Sent.READ, "1");

        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                writeSentSmsInternal(contentResolver, sentWriteListener, values);
                return null;
            }
        });
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

        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                writeInboxSmsInternal(resolver, inboxWriteListener, contentValues);
                return null;
            }
        });
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

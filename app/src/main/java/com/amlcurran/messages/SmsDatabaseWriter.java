package com.amlcurran.messages;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsDatabaseWriter {

    public SmsDatabaseWriter() {
    }

    private static ContentValues valuesFromMessage(SmsMessage message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Telephony.Sms.Inbox.BODY, message.getDisplayMessageBody());
        contentValues.put(Telephony.Sms.Inbox.ADDRESS, message.getDisplayOriginatingAddress());
        contentValues.put(Telephony.Sms.Inbox.DATE, message.getTimestampMillis());
        contentValues.put(Telephony.Sms.Inbox.DATE_SENT, message.getTimestampMillis());
        contentValues.put(Telephony.Sms.Inbox.TYPE, Telephony.Sms.Inbox.MESSAGE_TYPE_INBOX);
        return contentValues;
    }

    public boolean writeSentMessage(ContentResolver contentResolver, String address, String message, long sentDate) {

        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.Sent.DATE, sentDate);
        values.put(Telephony.Sms.Sent.DATE_SENT, sentDate);
        values.put(Telephony.Sms.Sent.ADDRESS, address);
        values.put(Telephony.Sms.Sent.BODY, message);
        values.put(Telephony.Sms.Sent.TYPE, Telephony.Sms.Sent.MESSAGE_TYPE_SENT);
        values.put(Telephony.Sms.Sent.READ, "1");

        return contentResolver.insert(Telephony.Sms.Sent.CONTENT_URI, values) != null;
    }

    public boolean writeInboxSms(ContentResolver resolver, SmsMessage message) {
        ContentValues contentValues = valuesFromMessage(message);
        Uri inserted = resolver.insert(Telephony.Sms.Inbox.CONTENT_URI, contentValues);
        return inserted != null;
    }
}

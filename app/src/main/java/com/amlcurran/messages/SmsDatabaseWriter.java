package com.amlcurran.messages;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.Telephony;

public class SmsDatabaseWriter {

    public SmsDatabaseWriter() {
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
}

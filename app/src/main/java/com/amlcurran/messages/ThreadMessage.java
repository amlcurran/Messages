package com.amlcurran.messages;

import android.database.Cursor;
import android.provider.Telephony;

import com.amlcurran.messages.adapters.CursorHelper;

public class ThreadMessage {
    private CharSequence body;
    private long timestamp;
    private boolean isFromMe;

    public static ThreadMessage fromCursor(Cursor item) {
        ThreadMessage message = new ThreadMessage();
        message.body = CursorHelper.fromColumn(item, Telephony.Sms.BODY);
        message.timestamp = CursorHelper.asLong(item, Telephony.Sms.DATE);
        message.isFromMe = CursorHelper.asInt(item, Telephony.Sms.TYPE) == Telephony.Sms.MESSAGE_TYPE_SENT;
        return message;
    }

    public CharSequence getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isFromMe() {
        return isFromMe;
    }
}

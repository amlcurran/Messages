package com.amlcurran.messages;

import android.database.Cursor;
import android.provider.Telephony;

import com.amlcurran.messages.adapters.CursorHelper;

public class Conversation {

    private String address;
    private String body;
    private String threadId;
    private boolean isRead;

    public static Conversation fromCursor(Cursor cursor) {
        Conversation conversation = new Conversation();
        conversation.address = CursorHelper.fromColumn(cursor, Telephony.Sms.ADDRESS);
        conversation.body = CursorHelper.fromColumn(cursor, Telephony.Sms.BODY);
        String s = CursorHelper.fromColumn(cursor, Telephony.Sms.Inbox.READ);
        conversation.isRead = s.toLowerCase().equals("1");
        conversation.threadId = CursorHelper.fromColumn(cursor, Telephony.Sms.THREAD_ID);
        return conversation;
    }

    public String getBody() {
        return body;
    }

    public String getAddress() {
        return address;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getThreadId() {
        return threadId;
    }
}

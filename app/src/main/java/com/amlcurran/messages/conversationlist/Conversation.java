package com.amlcurran.messages.conversationlist;

import android.database.Cursor;
import android.provider.Telephony;
import android.text.TextUtils;

import com.amlcurran.messages.adapters.CursorHelper;

public class Conversation {

    private String address;
    private String body;
    private String threadId;
    private boolean isRead;
    private String name;
    private long personId;

    public static Conversation fromCursor(Cursor cursor, long personId, String personName) {
        Conversation conversation = new Conversation();
        conversation.address = CursorHelper.fromColumn(cursor, Telephony.Sms.ADDRESS);
        conversation.body = CursorHelper.fromColumn(cursor, Telephony.Sms.BODY);
        String s = CursorHelper.fromColumn(cursor, Telephony.Sms.Inbox.READ);
        conversation.isRead = s.toLowerCase().equals("1");
        conversation.threadId = CursorHelper.fromColumn(cursor, Telephony.Sms.THREAD_ID);
        if (TextUtils.isEmpty(personName)) {
            conversation.name = conversation.address;
        } else {
            conversation.name = personName;
        }
        conversation.personId = personId;
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

    public String getName() {
        return name;
    }

    public long getPersonId() {
        return personId;
    }
}

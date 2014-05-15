package com.amlcurran.messages.loaders;

import android.app.Activity;
import android.database.Cursor;
import android.provider.Telephony;

public class StandardMessagesLoader implements MessagesLoader {

    private final Activity activity;

    public StandardMessagesLoader(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void loadConversationList(CursorLoadListener loadListener) {
        Cursor cursor = activity.getContentResolver().query(Telephony.Threads.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
        loadListener.onCursorLoaded(cursor);
    }

    @Override
    public void loadThread(String threadId, CursorLoadListener loadListener) {
        Cursor cursor = activity.getContentResolver().query(Telephony.Sms.CONTENT_URI, null,
                Telephony.Sms.THREAD_ID + "=?", new String[] { threadId }, Telephony.Sms.DEFAULT_SORT_ORDER);
        loadListener.onCursorLoaded(cursor);
    }

}

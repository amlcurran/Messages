package com.amlcurran.messages.loaders;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Telephony;

public class MessagesLoader {

    private final Activity activity;

    public MessagesLoader(Activity activity) {
        this.activity = activity;
    }

    private ContentResolver getResolver() {
        return activity.getContentResolver();
    }

    public void loadConversationList(CursorLoadListener loadListener) {
        Cursor cursor = getResolver().query(Telephony.Threads.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
        loadListener.onCursorLoaded(cursor);
    }

    public void loadThread(String threadId, CursorLoadListener loadListener) {
        String selection = Telephony.Sms.THREAD_ID + "=?";
        String[] selectionArgs = {threadId};
        Cursor cursor = getResolver().query(Telephony.Sms.CONTENT_URI, null, selection, selectionArgs, Telephony.Sms.DEFAULT_SORT_ORDER.replace("DESC", "ASC"));
        loadListener.onCursorLoaded(cursor);
    }

}

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
    public void loadConversationList(CursorLoadListener listener) {
        Cursor cursor = activity.getContentResolver().query(Telephony.Threads.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
        listener.onCursorLoaded(cursor);
    }

}

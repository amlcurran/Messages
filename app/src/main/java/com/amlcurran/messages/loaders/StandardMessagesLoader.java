package com.amlcurran.messages.loaders;

import android.app.Activity;
import android.database.Cursor;
import android.provider.Telephony;

public class StandardMessagesLoader implements MessagesLoader {

    private Activity context;

    public StandardMessagesLoader(Activity context) {
        this.context = context;
    }

    @Override
    public void loadConversationList(CursorLoadListener listener) {
        Cursor cursor = context.getContentResolver()
                .query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
        listener.onCursorLoaded(cursor);
    }

}

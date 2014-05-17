package com.amlcurran.messages.loaders;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Telephony;

import java.util.concurrent.ExecutorService;

public class MessagesLoader {

    private final Activity activity;
    private final ExecutorService executor;

    public MessagesLoader(Activity activity, ExecutorService executor) {
        this.activity = activity;
        this.executor = executor;
    }

    private ContentResolver getResolver() {
        return activity.getContentResolver();
    }

    public void loadConversationList(final CursorLoadListener loadListener) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = getResolver().query(Telephony.Threads.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
                activity.runOnUiThread(notifyListener(loadListener, cursor));
            }
        });
    }

    public void loadThread(final String threadId, final CursorLoadListener loadListener) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                String selection = Telephony.Sms.THREAD_ID + "=?";
                String[] selectionArgs = {threadId};
                final Cursor cursor = getResolver().query(Telephony.Sms.CONTENT_URI, null, selection, selectionArgs, Telephony.Sms.DEFAULT_SORT_ORDER.replace("DESC", "ASC"));
                activity.runOnUiThread(notifyListener(loadListener, cursor));
            }
        });
    }

    private static Runnable notifyListener(final CursorLoadListener listener, final Cursor result) {
        return new Runnable() {
            @Override
            public void run() {
                listener.onCursorLoaded(result);
            }
        };
    }

}

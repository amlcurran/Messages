package com.amlcurran.messages.loaders;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;

import com.amlcurran.messages.adapters.CursorHelper;

import java.util.Arrays;
import java.util.concurrent.Callable;
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
        executor.submit(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                Cursor conversationsList = getResolver().query(Telephony.Threads.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
//                Cursor peopleList = queryPeople(conversationsList);
//                //peopleList.getExtras().putInt("key", 1);
//
//                while (peopleList.moveToNext()) {
//                    Log.d("Testing", CursorHelper.fromColumn(peopleList, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
//                }

                activity.runOnUiThread(notifyListener(loadListener, conversationsList));
                //activity.runOnUiThread(notifyListener(loadListener, peopleList));
                return null;
            }
        });
    }

    private Cursor queryPeople(Cursor conversationsList) {
        String personSelection = createPersonSelectionString(conversationsList);
        String[] personSelectArgs = createPersonSelectArgs(conversationsList);
        resetCursorPosition(conversationsList);
        String[] projection = new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY };

        return getResolver().query(ContactsContract.Data.CONTENT_URI, projection, personSelection, personSelectArgs, null);
    }

    private boolean resetCursorPosition(Cursor conversationsList) {
        return conversationsList.moveToPosition(-1);
    }

    private String[] createPersonSelectArgs(Cursor conversationsList) {
        String[] firstPass = new String[conversationsList.getCount()];
        int currentPosition = 0;
        resetCursorPosition(conversationsList);
        while (conversationsList.moveToNext()) {
            if (isValidPersonId(conversationsList)) {
                firstPass[currentPosition] = CursorHelper.fromColumn(conversationsList, Telephony.Sms.PERSON);
                currentPosition++;
            }
        }

        String[] result = Arrays.copyOf(firstPass, currentPosition);

        Log.d("Selection string", "" + currentPosition + " result length " + result.length );
        return result;
    }

    private String createPersonSelectionString(Cursor conversationsList) {
        String personSelection = "";
        int count = 0;
        resetCursorPosition(conversationsList);
        while (conversationsList.moveToNext()) {
            if (isValidPersonId(conversationsList)) {
                personSelection += ContactsContract.Contacts.LOOKUP_KEY + "=? OR ";
                count++;
            }
        }
        Log.d("Selection string", "" + count);
        if (personSelection.length() > 0) {
            personSelection = personSelection.substring(0, personSelection.length() - 4);
        }
        return personSelection;
    }

    private boolean isValidPersonId(Cursor conversationsList) {
        return CursorHelper.asInt(conversationsList, Telephony.Sms.PERSON) != -1  && !TextUtils.isEmpty(CursorHelper.fromColumn(conversationsList, Telephony.Sms.PERSON));
    }

    public void loadThread(final String threadId, final CursorLoadListener loadListener) {
        executor.submit(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                String selection = Telephony.Sms.THREAD_ID + "=?";
                String[] selectionArgs = {threadId};
                final Cursor cursor = getResolver().query(Telephony.Sms.CONTENT_URI, null, selection, selectionArgs, Telephony.Sms.DEFAULT_SORT_ORDER.replace("DESC", "ASC"));
                activity.runOnUiThread(notifyListener(loadListener, cursor));
                return null;
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

    public void markThreadAsRead(final String threadId) {
        executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                ContentValues contentValues = getReadContentValues();
                String selection = String.format("%1$s=? AND %2$s=?", Telephony.Sms.THREAD_ID, Telephony.Sms.READ);
                String[] args = new String[] { threadId, "0" };
                getResolver().update(Telephony.Sms.CONTENT_URI, contentValues, selection, args);
                return null;
            }

            private ContentValues getReadContentValues() {
                ContentValues values = new ContentValues();
                values.put(Telephony.Sms.READ, "1");
                return values;
            }
        });
    }
}

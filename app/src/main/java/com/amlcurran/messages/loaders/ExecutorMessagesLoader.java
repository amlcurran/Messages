/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlcurran.messages.loaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;

import com.amlcurran.messages.R;
import com.amlcurran.messages.adapters.CursorHelper;
import com.amlcurran.messages.conversationlist.Conversation;
import com.amlcurran.messages.conversationlist.ConversationListListener;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class ExecutorMessagesLoader implements MessagesLoader {

    private final Context activity;
    private final ExecutorService executor;

    public ExecutorMessagesLoader(Context context, ExecutorService executor) {
        this.activity = context;
        this.executor = executor;
    }

    private ContentResolver getResolver() {
        return activity.getContentResolver();
    }

    @Override
    public void loadConversationList(final ConversationListListener loadListener) {
        executor.submit(createLoadConversationList(loadListener, null, null));
    }

    private Callable<Object> createLoadConversationList(final ConversationListListener loadListener, final String query, final String[] args) {
        return new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                final List<Conversation> conversations = new ArrayList<Conversation>();
                Cursor conversationsList = getResolver().query(Telephony.Threads.CONTENT_URI, null, query, args, Telephony.Sms.DEFAULT_SORT_ORDER);

                while (conversationsList.moveToNext()) {
                    Uri phoneLookupUri = createPhoneLookupUri(conversationsList);
                    Cursor peopleCursor = getResolver().query(phoneLookupUri, null, null, null, null);

                    String person = getPersonName(peopleCursor);
                    long personId = getContactId(peopleCursor);
                    Conversation conversation = Conversation.fromCursor(conversationsList, personId, person);

                    conversations.add(conversation);
                    peopleCursor.close();
                }

                conversationsList.close();

                loadListener.onConversationListLoaded(conversations);
                return null;
            }
        };
    }

    private static long getContactId(Cursor peopleCursor) {
        long id = -1;
        if (peopleCursor.moveToFirst()) {
            id = CursorHelper.asLong(peopleCursor, ContactsContract.Contacts.PHOTO_ID);
        }
        return id;
    }

    private static Uri createPhoneLookupUri(Cursor conversationsList) {
        String phoneRaw = CursorHelper.fromColumn(conversationsList, Telephony.Sms.ADDRESS);
        return Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(phoneRaw));
    }

    private static String getPersonName(Cursor peopleCursor) {
        String result = null;
        if (peopleCursor.moveToFirst()) {
            result = CursorHelper.fromColumn(peopleCursor, ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME_PRIMARY);
        }
        return result;
    }

    @Override
    public void loadThread(final String threadId, final CursorLoadListener loadListener) {
        executor.submit(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                String selection = Telephony.Sms.THREAD_ID + "=?";
                String[] selectionArgs = {threadId};
                final Cursor cursor = getResolver().query(Telephony.Sms.CONTENT_URI, null, selection, selectionArgs, Telephony.Sms.DEFAULT_SORT_ORDER.replace("DESC", "ASC"));
                loadListener.onCursorLoaded(cursor);
                return null;
            }
        });
    }

    @Override
    public void markThreadAsRead(final String threadId) {
        executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                String selection = String.format("%1$s=? AND %2$s=?", Telephony.Sms.THREAD_ID, Telephony.Sms.READ);
                String[] args = new String[]{ threadId , "0" };
                getResolver().update(Telephony.Sms.CONTENT_URI, createReadContentValues(), selection, args);
                return null;
            }

        });
    }

    @Override
    public void loadPhoto(final long contactId, final PhotoLoadListener photoLoadListener) {
        executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                Bitmap result = null;
                if (contactId >= 0) {

                    Uri contactUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, contactId);
                    Cursor cursor = activity.getContentResolver().query(contactUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
                    if (cursor.moveToFirst()) {
                        try {
                            byte[] blob = cursor.getBlob(0);
                            result = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                        } finally {
                            cursor.close();
                        }
                    }
                }

                if (result == null) {
                    result = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.ic_contact_picture_unknown)).getBitmap();
                }

                photoLoadListener.onPhotoLoaded(result);
                return null;
            }
        });
    }

    @Override
    public void loadUnreadConversationList(ConversationListListener loadListener) {
        executor.submit(createLoadConversationList(loadListener, Telephony.Sms.Inbox.READ + "=0", null));
    }

    @Override
    public void cancelAll() {
        executor.shutdownNow();
    }

    private static ContentValues createReadContentValues() {
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.READ, "1");
        return values;
    }

}

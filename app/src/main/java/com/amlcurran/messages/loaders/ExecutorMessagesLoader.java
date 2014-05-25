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
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;

import com.amlcurran.messages.OnContactQueryListener;
import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationListListener;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.data.Conversation;
import com.espian.utils.CursorHelper;

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
        executor.submit(new ConversationListTask(getResolver(), null, null, loadListener));
    }

    static Uri createPhoneLookupUri(String phoneRaw) {
        return Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(phoneRaw));
    }

    @Override
    public void loadThread(final String threadId, final CursorLoadListener loadListener) {
        executor.submit(new ThreadTask(getResolver(), threadId, Telephony.Sms.CONTENT_URI, loadListener));
    }

    @Override
    public void markThreadAsRead(final String threadId) {
        executor.submit(new MarkReadTask(getResolver(), threadId));
    }

    @Override
    public void loadPhoto(final long contactId, final PhotoLoadListener photoLoadListener) {
        Bitmap defaultImage = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.ic_contact_picture_unknown)).getBitmap();
        executor.submit(new PhotoLoadTask(getResolver(), contactId, photoLoadListener, defaultImage));
    }

    @Override
    public void loadUnreadConversationList(ConversationListListener loadListener) {
        String selection = Telephony.Mms.Inbox.READ + "=0";
        executor.submit(new ConversationListTask(getResolver(), selection, null, loadListener));
    }

    @Override
    public void cancelAll() {
        executor.shutdownNow();
    }

    @Override
    public void queryContact(final String address, final OnContactQueryListener onContactQueryListener) {
        executor.submit(new SingleContactTask(getResolver(), address, onContactQueryListener));
    }

    @Override
    public void deleteThread(final Conversation conversation, final OnThreadDeleteListener threadDeleteListener) {
        executor.submit(new DeleteThreadTask(getResolver(), conversation, threadDeleteListener));
    }

    @Override
    public void markThreadAsUnread(final String threadId) {
        executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                executor.submit(new ThreadTask(getResolver(), threadId, Telephony.Sms.Inbox.CONTENT_URI, new CursorLoadListener() {
                            @Override
                            public void onCursorLoaded(Cursor cursor) {
                                if (cursor.moveToLast()) {

                                    // This updates an unread message
                                    String selection = String.format("%1$s=? AND %2$s=?", Telephony.Sms.THREAD_ID, Telephony.Sms._ID);
                                    String[] args = new String[]{threadId, CursorHelper.asString(cursor, Telephony.Sms._ID)};
                                    getResolver().update(Telephony.Sms.Inbox.CONTENT_URI, createUnreadContentValues(), selection, args);

                                }
                            }
                        }));
                return null;
            }

        });
    }

    private ContentValues createUnreadContentValues() {
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.READ, "0");
        return values;
    }

}

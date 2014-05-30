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
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;

import com.amlcurran.messages.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.data.Sort;
import com.espian.utils.data.CursorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class ConversationListTask implements Callable<Object> {

    private final ContentResolver contentResolver;
    private final String query;
    private final String[] args;
    private final ConversationListListener loadListener;
    private final Sort sort;

    ConversationListTask(ContentResolver contentResolver, String query, String[] args, ConversationListListener loadListener, Sort sort) {
        this.contentResolver = contentResolver;
        this.query = query;
        this.args = args;
        this.loadListener = loadListener;
        this.sort = sort;
    }

    public ConversationListTask(ContentResolver contentResolver, ConversationListListener loadListener, Sort sort) {
        this(contentResolver, null, null, loadListener, sort);
    }

    @Override
    public Object call() throws Exception {
        final List<Conversation> conversations = new ArrayList<Conversation>();
        Cursor conversationsList = contentResolver.query(Telephony.Threads.CONTENT_URI, null, query, args, getSortString());

        while (conversationsList.moveToNext()) {
            Uri phoneLookupUri = createPhoneLookupUri(conversationsList);
            Cursor peopleCursor = contentResolver.query(phoneLookupUri, null, null, null, null);

            Contact contact = ContactFactory.fromCursor(peopleCursor, CursorHelper.asString(conversationsList, Telephony.Sms.ADDRESS));
            String address = CursorHelper.asString(conversationsList, Telephony.Sms.ADDRESS);
            String body = CursorHelper.asString(conversationsList, Telephony.Sms.BODY);
            String s = CursorHelper.asString(conversationsList, Telephony.Sms.Inbox.READ);
            boolean isRead = s.toLowerCase().equals("1");
            String threadId = CursorHelper.asString(conversationsList, Telephony.Sms.THREAD_ID);
            Conversation conversation = new Conversation(address, body, threadId, isRead, contact);

            conversations.add(conversation);
            peopleCursor.close();
        }

        conversationsList.close();

        loadListener.onConversationListLoaded(conversations);
        return null;
    }

    private String getSortString() {
        String sortOrder;
        if (sort == Sort.UNREAD) {
            sortOrder = Telephony.Sms.READ + " ASC, " + Telephony.Sms.DEFAULT_SORT_ORDER;
        } else {
            sortOrder = Telephony.Sms.DEFAULT_SORT_ORDER;
        }
        return sortOrder;
    }

    private static long getContactPhotoId(Cursor peopleCursor) {
        long id = -1;
        if (peopleCursor.moveToFirst()) {
            id = CursorHelper.asLong(peopleCursor, ContactsContract.Contacts.PHOTO_ID);
        }
        return id;
    }

    private static Uri createPhoneLookupUri(Cursor conversationsList) {
        String phoneRaw = CursorHelper.asString(conversationsList, Telephony.Sms.ADDRESS);
        return SingleContactTask.createPhoneLookupUri(phoneRaw);
    }

    private static String getPersonName(Cursor peopleCursor) {
        String result = null;
        if (peopleCursor.moveToFirst()) {
            result = CursorHelper.asString(peopleCursor, ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME_PRIMARY);
        }
        return result;
    }

}

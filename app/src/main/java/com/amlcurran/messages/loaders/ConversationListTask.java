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
import android.provider.Telephony;

import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.data.ContactFactory;
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
            String address = CursorHelper.asString(conversationsList, Telephony.Sms.ADDRESS);

            Contact contact = getContact(address);

            String body = CursorHelper.asString(conversationsList, Telephony.Sms.BODY);
            String s = CursorHelper.asString(conversationsList, Telephony.Sms.Inbox.READ);
            boolean isRead = s.toLowerCase().equals("1");
            String threadId = CursorHelper.asString(conversationsList, Telephony.Sms.THREAD_ID);
            Conversation conversation = new Conversation(address, body, threadId, isRead, contact);

            if (conversation.getThreadId() != null) {
                conversations.add(conversation);
            }
        }

        conversationsList.close();

        loadListener.onConversationListLoaded(conversations);
        return null;
    }

    private Contact getContact(String address) {
        Uri phoneLookupUri = createPhoneLookupUri(address);
        Cursor peopleCursor = contentResolver.query(phoneLookupUri, ContactFactory.VALID_PROJECTION, null, null, null);

        Contact contact;
        if (peopleCursor.moveToFirst()) {
            contact = ContactFactory.fromCursor(peopleCursor);
        } else {
            contact = ContactFactory.fromAddress(address);
        }
        peopleCursor.close();
        return contact;
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

    private static Uri createPhoneLookupUri(String phoneRaw) {
        return SingleContactTask.createPhoneLookupUri(phoneRaw);
    }

}

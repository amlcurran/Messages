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

package com.amlcurran.messages;

import android.content.Context;

import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.RawContact;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.core.loaders.ConversationListChangeListener;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.OnContactQueryListener;
import com.amlcurran.messages.loaders.OnThreadDeleteListener;

import java.util.Collections;
import java.util.List;

public class DemoMessagesLoader implements MessagesLoader {
    private Context context;

    public DemoMessagesLoader(Context context) {
        this.context = context;
    }

    @Override
    public void loadConversationList(ConversationListListener loadListener, Sort sort) {
        Conversation conversation = new Conversation("+444", "Hello! This is body", "1", true, new RawContact("+44"));
        loadListener.onConversationListLoaded(Collections.singletonList(conversation));
    }

    @Override
    public void loadThread(String threadId, ThreadListener threadListener) {

    }

    @Override
    public void markThreadAsRead(String threadId, ConversationListChangeListener conversationListChangeListener) {

    }

    @Override
    public void loadPhoto(Contact contact, PhotoLoadListener photoLoadListener) {

    }

    @Override
    public void loadUnreadConversationList(ConversationListListener loadListener) {

    }

    @Override
    public void cancelAll() {

    }

    @Override
    public void queryContact(String address, OnContactQueryListener onContactQueryListener) {

    }

    @Override
    public void deleteThreads(List<Conversation> conversation, OnThreadDeleteListener threadDeleteListener) {

    }

    @Override
    public void markThreadAsUnread(List<Conversation> threadId, ConversationListChangeListener changeListener) {

    }

    @Override
    public void loadContacts(ContactListListener contactListListener) {

    }
}

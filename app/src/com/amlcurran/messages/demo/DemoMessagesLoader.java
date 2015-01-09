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

package com.amlcurran.messages.demo;

import android.content.Context;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.SavedContact;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.core.loaders.OnContactQueryListener;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.data.ParcelablePhoneNumber;
import com.amlcurran.messages.core.conversationlist.ConversationLoader;
import com.amlcurran.messages.core.conversationlist.HasConversationListener;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.core.loaders.Task;

import java.util.ArrayList;
import java.util.List;

public class DemoMessagesLoader implements MessagesLoader, ConversationLoader {
    private Context context;

    public DemoMessagesLoader(Context context) {
        this.context = context;
    }

    @Override
    public void loadConversationList(ConversationListListener loadListener) {
        List<Conversation> fakeConversations = new ArrayList<Conversation>();
        String[] numbers = getStringArray(R.array.demo_numbers);
        String[] bodies = getStringArray(R.array.demo_bodies);
        String[] contactNames = getStringArray(R.array.demo_contact_names);
        for (int i = 0; i < numbers.length; i++) {
            PhoneNumber number = new ParcelablePhoneNumber(numbers[i]);
            Contact contact = new SavedContact(i, contactNames[i], number, i, String.valueOf(i), 0);
            Conversation conversation = new Conversation(number, bodies[i], String.valueOf(i), i != 4, contact, i == 2, Time.fromMillis(0), (int) (Math.random() * 100));
            fakeConversations.add(conversation);
        }
        loadListener.onConversationListLoaded(fakeConversations);
    }

    private String[] getStringArray(int id) {
        return context.getResources().getStringArray(id);
    }

    @Override
    public void loadThread(String threadId, ThreadListener threadListener) {
    }

    @Override
    public void markThreadAsRead(String threadId) {

    }

    @Override
    public void loadUnreadConversationList(ConversationListListener loadListener) {

    }

    @Override
    public void cancelAll() {

    }

    @Override
    public void queryContact(PhoneNumber address, OnContactQueryListener onContactQueryListener) {

    }

    @Override
    public void deleteConversations(List<Conversation> conversation) {

    }

    @Override
    public void markThreadsAsUnread(List<String> threadId) {

    }

    @Override
    public void loadContacts(ContactListListener contactListListener) {

    }

    @Override
    public void getHasConversationWith(Contact contact, HasConversationListener hasConversationListener) {

    }

    @Override
    public Task loadUnreadMessages(ThreadListener threadListener) {
        return null;
    }
}

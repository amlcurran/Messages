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

import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.loaders.ConversationListChangeListener;
import com.amlcurran.messages.core.loaders.ThreadListener;

import java.util.List;

public interface MessagesLoader {

    void loadConversationList(ConversationListListener loadListener, Sort sort);

    void loadThread(String threadId, ThreadListener threadListener);

    void markThreadAsRead(String threadId, ConversationListChangeListener conversationListChangeListener);

    void loadPhoto(Contact contact, PhotoLoadListener photoLoadListener);

    void loadUnreadConversationList(ConversationListListener loadListener);

    void cancelAll();

    void queryContact(String address, OnContactQueryListener onContactQueryListener);

    void deleteThreads(List<Conversation> conversation, OnThreadDeleteListener threadDeleteListener);

    void markThreadAsUnread(List<Conversation> threadId, ConversationListChangeListener changeListener);

    void loadContacts(ContactListListener contactListListener);
}

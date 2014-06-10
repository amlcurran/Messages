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
import android.content.Context;

import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.loaders.ConversationListChangeListener;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.events.EventBus;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class ExecutorMessagesLoader implements MessagesLoader {

    private final Context context;
    private final ExecutorService executor;
    private final MessagesCache cache;
    private final EventBus eventBus;

    public ExecutorMessagesLoader(Context context, ExecutorService executor, MessagesCache cache, EventBus eventBus) {
        this.context = context;
        this.executor = executor;
        this.cache = cache;
        this.eventBus = eventBus;
    }

    private ContentResolver getResolver() {
        return context.getContentResolver();
    }

    private void submit(final Callable task) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    task.call();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void loadConversationList(ConversationListListener loadListener, Sort sort) {
        if (cache.getConversationList() == null) {
            submit(new ConversationListTask(getResolver(), new ConversationListListener() {
                @Override
                public void onConversationListLoaded(List<Conversation> conversations) {
                    eventBus.postListLoaded();
                }
            }, sort, cache));
        } else {
            loadListener.onConversationListLoaded(cache.getConversationList());
        }
    }

    @Override
    public void loadThread(String threadId, ThreadListener threadListener) {
        submit(new ThreadTask(getResolver(), threadId, threadListener));
    }

    @Override
    public void markThreadAsRead(String threadId, ConversationListChangeListener listChangeListener) {
        submit(new MarkReadTask(getResolver(), threadId, listChangeListener));
    }

    @Override
    public void loadPhoto(Contact contact, PhotoLoadListener photoLoadListener) {
        submit(new PhotoLoadTask(getResolver(), context.getResources(), contact, photoLoadListener));
    }

    @Override
    public void loadUnreadConversationList(ConversationListListener loadListener) {
        submit(new UnreadConversationListTask(getResolver(), loadListener));
    }

    @Override
    public void cancelAll() {
        executor.shutdownNow();
    }

    @Override
    public void queryContact(String address, OnContactQueryListener onContactQueryListener) {
        submit(new SingleContactTask(getResolver(), address, onContactQueryListener));
    }

    @Override
    public void deleteThreads(List<Conversation> conversationList, OnThreadDeleteListener threadDeleteListener) {
        submit(new DeleteThreadTask(getResolver(), conversationList, threadDeleteListener));
    }

    @Override
    public void markThreadAsUnread(List<Conversation> conversations, ConversationListChangeListener changeListener) {
        submit(new MarkUnreadTask(getResolver(), conversations, changeListener));
    }

    @Override
    public void loadContacts(ContactListListener contactListListener) {
        submit(new ContactsTask(getResolver(), contactListListener));
    }

}

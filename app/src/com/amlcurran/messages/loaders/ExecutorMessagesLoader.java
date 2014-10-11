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
import android.os.Handler;

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.core.loaders.ConversationListChangeListener;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.core.events.EventBus;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ExecutorMessagesLoader implements MessagesLoader {

    private final Context context;
    private final ExecutorService executor;
    private final MessagesCache cache;
    private final EventBus eventBus;
    private final Handler uiHandler;

    public ExecutorMessagesLoader(Context context, ExecutorService executor, MessagesCache cache, EventBus eventBus, Handler uiHandler) {
        this.context = context;
        this.executor = executor;
        this.cache = cache;
        this.eventBus = eventBus;
        this.uiHandler = uiHandler;
    }

    private ContentResolver getResolver() {
        return context.getContentResolver();
    }

    private Task submit(final Callable task) {
        Future result = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    task.call();
                } catch (Exception e) {
                    MessagesLog.e(ExecutorMessagesLoader.this, e);
                    throw new RuntimeException(e);
                }
            }
        });
        return new FutureTask(result);
    }

    @Override
    public void loadConversationList(final ConversationListListener loadListener, Sort sort) {
        if (cache.getConversationList() == null) {
            submit(new ConversationListTask(getResolver(), new ConversationListListener() {
                @Override
                public void onConversationListLoaded(List<Conversation> conversations) {
                    postConversationListLoaded(loadListener);
                    eventBus.postListLoaded();
                }
            }, sort, cache));
        } else {
            postConversationListLoaded(loadListener);
        }
    }

    private void postConversationListLoaded(final ConversationListListener loadListener) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                loadListener.onConversationListLoaded(cache.getConversationList());
            }
        });
    }

    @Override
    public void loadThread(String threadId, ThreadListener threadListener) {
        submit(new ThreadTask(getResolver(), threadId, threadListener, uiHandler));
    }

    @Override
    public void markThreadAsRead(String threadId, ConversationListChangeListener listChangeListener) {
        submit(new MarkReadTask(getResolver(), eventBus, Collections.singletonList(threadId)));
    }

    @Override
    public Task loadPhoto(Contact contact, PhotoLoadListener photoLoadListener) {
        return submit(new PhotoLoadTask(getResolver(), context.getResources(), contact, photoLoadListener, cache, uiHandler));
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
    public void queryContact(PhoneNumber phoneNumber, OnContactQueryListener onContactQueryListener) {
        submit(new ContactTask(getResolver(), phoneNumber.flatten(), onContactQueryListener, uiHandler));
    }

    @Override
    public void deleteThreads(List<Conversation> conversationList, OnThreadDeleteListener threadDeleteListener) {
        submit(new DeleteThreadTask(getResolver(), conversationList, threadDeleteListener, uiHandler));
    }

    @Override
    public void markThreadAsUnread(List<Conversation> conversations, ConversationListChangeListener changeListener) {
        submit(new MarkUnreadTask(getResolver(), conversations, changeListener, uiHandler));
    }

    @Override
    public void loadContacts(ContactListListener contactListListener) {
        submit(new ContactsTask(getResolver(), contactListListener, uiHandler));
    }

    @Override
    public void getHasConversationWith(Contact contact, HasConversationListener hasConversationListener) {
        submit(new HasConversationTask(getResolver(), hasConversationListener, contact, uiHandler));
    }

    @Override
    public Task loadUnreadMessages(ThreadListener threadListener) {
        return submit(new UnreadMessagesTask(getResolver(), threadListener, uiHandler));
    }

}

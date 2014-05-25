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

import com.amlcurran.messages.OnContactQueryListener;
import com.amlcurran.messages.conversationlist.ConversationListListener;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.data.Conversation;

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

    private void submit(Callable task) {
        executor.submit(task);
    }

    @Override
    public void loadConversationList(final ConversationListListener loadListener) {
        submit(new ConversationListTask(getResolver(), loadListener));
    }

    @Override
    public void loadThread(final String threadId, final CursorLoadListener loadListener) {
        submit(new ThreadTask(getResolver(), threadId, loadListener));
    }

    @Override
    public void markThreadAsRead(final String threadId) {
        submit(new MarkReadTask(getResolver(), threadId));
    }

    @Override
    public void loadPhoto(final long contactId, final PhotoLoadListener photoLoadListener) {
        submit(new PhotoLoadTask(getResolver(), activity.getResources(), contactId, photoLoadListener));
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
    public void queryContact(final String address, final OnContactQueryListener onContactQueryListener) {
        submit(new SingleContactTask(getResolver(), address, onContactQueryListener));
    }

    @Override
    public void deleteThreads(final List<Conversation> conversationList, final OnThreadDeleteListener threadDeleteListener) {
        submit(new DeleteThreadTask(getResolver(), conversationList, threadDeleteListener));
    }

    @Override
    public void markThreadAsUnread(final List<Conversation> conversations) {
        submit(new MarkUnreadTask(getResolver(), conversations));
    }

}

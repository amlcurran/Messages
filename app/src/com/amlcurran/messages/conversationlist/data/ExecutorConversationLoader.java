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

package com.amlcurran.messages.conversationlist.data;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.loaders.FutureTask;
import com.amlcurran.messages.loaders.Task;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ExecutorConversationLoader implements ConversationLoader {

    private final ExecutorService executor;
    private final Context context;
    private final Handler uiHandler;
    private final ContentResolver resolver;

    public ExecutorConversationLoader(ExecutorService executor, Context context, Handler uiHandler) {
        this.executor = executor;
        this.context = context;
        this.uiHandler = uiHandler;
        this.resolver = context.getContentResolver();
    }

    private Task submit(final Callable task) {
        Future result = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    task.call();
                } catch (Exception e) {
                    MessagesLog.e(ExecutorConversationLoader.this, e);
                    throw new RuntimeException(e);
                }
            }
        });
        return new FutureTask(result);
    }

    @Override
    public void loadConversationList(final ConversationListListener loadListener, Sort sort) {
        submit(new ConversationListTask(resolver, loadListener, sort));
    }

    @Override
    public void loadUnreadConversationList(ConversationListListener loadListener) {
        submit(new UnreadConversationListTask(resolver, loadListener));
    }

    @Override
    public void deleteConversations(List<Conversation> conversationList) {
        submit(new DeleteThreadTask(resolver, conversationList, SingletonManager.getConversationList(context)));
    }

    @Override
    public void getHasConversationWith(Contact contact, HasConversationListener hasConversationListener) {
        submit(new HasConversationTask(resolver, hasConversationListener, contact, uiHandler));
    }
}

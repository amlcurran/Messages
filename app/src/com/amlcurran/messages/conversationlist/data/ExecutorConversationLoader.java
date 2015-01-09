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

import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.CommandQueue;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.conversationlist.ConversationLoader;
import com.amlcurran.messages.core.conversationlist.HasConversationListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.loaders.TaskQueue;

import java.util.List;

public class ExecutorConversationLoader implements ConversationLoader {

    private final TaskQueue taskQueue;
    private final Context context;
    private final CommandQueue uiCommandQueue;
    private final ContentResolver resolver;

    public ExecutorConversationLoader(TaskQueue taskQueue, Context context, CommandQueue uiCommandQueue) {
        this.taskQueue = taskQueue;
        this.context = context;
        this.uiCommandQueue = uiCommandQueue;
        this.resolver = context.getContentResolver();
    }

    @Override
    public void loadConversationList(final ConversationListListener loadListener) {
        taskQueue.submit(new ConversationListTask(resolver, loadListener));
    }

    @Override
    public void loadUnreadConversationList(ConversationListListener loadListener) {
        taskQueue.submit(new UnreadConversationListTask(resolver, loadListener));
    }

    @Override
    public void deleteConversations(List<Conversation> conversationList) {
        taskQueue.submit(new DeleteThreadTask(resolver, conversationList, SingletonManager.getConversationList(context)));
    }

    @Override
    public void getHasConversationWith(Contact contact, HasConversationListener hasConversationListener) {
        taskQueue.submit(new HasConversationTask(resolver, hasConversationListener, contact, uiCommandQueue));
    }
}

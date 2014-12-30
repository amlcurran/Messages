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
import android.provider.Telephony;

import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.core.loaders.OnContactQueryListener;
import com.amlcurran.messages.core.loaders.TaskQueue;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.core.loaders.Task;

import java.util.Collections;
import java.util.List;

public class TaskQueueMessagesLoader implements MessagesLoader {

    private final Context context;
    private final Handler uiHandler;
    private final TaskQueue taskQueue;

    public TaskQueueMessagesLoader(Context context, TaskQueue taskQueue, Handler uiHandler) {
        this.context = context;
        this.taskQueue = taskQueue;
        this.uiHandler = uiHandler;
    }

    private ContentResolver getResolver() {
        return context.getContentResolver();
    }

    @Override
    public void loadThread(String threadId, ThreadListener threadListener) {
        taskQueue.submit(new ThreadTask(getResolver(), threadId, Telephony.Sms.CONTENT_URI, threadListener, uiHandler));
    }

    @Override
    public void markThreadAsRead(String threadId) {
        taskQueue.submit(new MarkReadTask(getResolver(), SingletonManager.getConversationList(context), Collections.singletonList(threadId)));
    }

    @Override
    public void cancelAll() {
        taskQueue.cancelAll();
    }

    @Override
    public void queryContact(PhoneNumber phoneNumber, OnContactQueryListener onContactQueryListener) {
        taskQueue.submit(new ContactTask(getResolver(), phoneNumber.flatten(), onContactQueryListener, uiHandler));
    }

    @Override
    public void markThreadsAsUnread(List<String> threadIds) {
        taskQueue.submit(new MarkUnreadTask(getResolver(), SingletonManager.getConversationList(context), threadIds));
    }

    @Override
    public void loadContacts(ContactListListener contactListListener) {
        taskQueue.submit(new ContactsTask(getResolver(), contactListListener, uiHandler));
    }

    @Override
    public Task loadUnreadMessages(ThreadListener threadListener) {
        return taskQueue.submit(new UnreadMessagesTask(getResolver(), threadListener, uiHandler));
    }

}

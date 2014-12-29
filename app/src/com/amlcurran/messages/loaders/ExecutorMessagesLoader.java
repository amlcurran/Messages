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

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.core.loaders.OnContactQueryListener;
import com.amlcurran.messages.core.loaders.ThreadListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ExecutorMessagesLoader implements MessagesLoader {

    private final Context context;
    private final ExecutorService executor;
    private final Handler uiHandler;

    public ExecutorMessagesLoader(Context context, ExecutorService executor, Handler uiHandler) {
        this.context = context;
        this.executor = executor;
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
    public void loadThread(String threadId, ThreadListener threadListener) {
        submit(new ThreadTask(getResolver(), threadId, Telephony.Sms.CONTENT_URI, threadListener, uiHandler));
    }

    @Override
    public void markThreadAsRead(String threadId) {
        submit(new MarkReadTask(getResolver(), SingletonManager.getConversationList(context), Collections.singletonList(threadId)));
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
    public void markThreadsAsUnread(List<String> threadIds) {
        submit(new MarkUnreadTask(getResolver(), SingletonManager.getConversationList(context), threadIds));
    }

    @Override
    public void loadContacts(ContactListListener contactListListener) {
        submit(new ContactsTask(getResolver(), contactListListener, uiHandler));
    }

    @Override
    public Task loadUnreadMessages(ThreadListener threadListener) {
        return submit(new UnreadMessagesTask(getResolver(), threadListener, uiHandler));
    }

}

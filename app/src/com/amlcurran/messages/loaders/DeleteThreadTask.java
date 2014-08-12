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
import android.os.Handler;
import android.provider.Telephony;

import com.amlcurran.messages.core.data.Conversation;

import java.util.List;
import java.util.concurrent.Callable;

class DeleteThreadTask implements Callable<Object> {
    private final ContentResolver contentResolver;
    private final List<Conversation> conversationList;
    private final OnThreadDeleteListener threadDeleteListener;
    private Handler uiHandler;

    public DeleteThreadTask(ContentResolver contentResolver, List<Conversation> conversationList, OnThreadDeleteListener threadDeleteListener, Handler uiHandler) {
        this.contentResolver = contentResolver;
        this.conversationList = conversationList;
        this.threadDeleteListener = threadDeleteListener;
        this.uiHandler = uiHandler;
    }

    @Override
    public Object call() throws Exception {
        String where = Telephony.Sms.THREAD_ID + "=?";
        for (Conversation conversation : conversationList) {
            String[] args = new String[]{conversation.getThreadId()};
            contentResolver.delete(Telephony.Sms.CONTENT_URI, where, args);
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                threadDeleteListener.threadDeleted(conversationList);
            }
        });
        return null;
    }
}

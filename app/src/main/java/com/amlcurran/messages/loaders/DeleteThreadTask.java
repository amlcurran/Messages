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
import android.provider.Telephony;

import com.amlcurran.messages.data.Conversation;

import java.util.concurrent.Callable;

class DeleteThreadTask implements Callable<Object> {
    private final ContentResolver contentResolver;
    private final Conversation conversation;
    private final OnThreadDeleteListener threadDeleteListener;

    public DeleteThreadTask(ContentResolver contentResolver, Conversation conversation, OnThreadDeleteListener threadDeleteListener) {
        this.contentResolver = contentResolver;
        this.conversation = conversation;
        this.threadDeleteListener = threadDeleteListener;
    }

    @Override
    public Object call() throws Exception {
        String where = Telephony.Sms.THREAD_ID + "=?";
        String[] args = new String[] {conversation.getThreadId()};
        contentResolver.delete(Telephony.Sms.CONTENT_URI, where, args);
        threadDeleteListener.threadDeleted(conversation);
        return null;
    }
}

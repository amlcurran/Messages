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
import android.database.Cursor;
import android.provider.Telephony;

import com.amlcurran.messages.core.CommandQueue;
import com.amlcurran.messages.core.conversationlist.HasConversationListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.sourcebinder.CursorHelper;

import java.util.concurrent.Callable;

class HasConversationTask implements Callable {
    private final ContentResolver resolver;
    private final HasConversationListener hasConversationListener;
    private final Contact contact;
    private final CommandQueue uiHandler;

    public HasConversationTask(ContentResolver resolver, HasConversationListener hasConversationListener, Contact contact, CommandQueue uiHandler) {
        this.resolver = resolver;
        this.hasConversationListener = hasConversationListener;
        this.contact = contact;
        this.uiHandler = uiHandler;
    }

    @Override
    public Object call() throws Exception {
        String selection = Telephony.Sms.ADDRESS + "=?";
        String[] selectionArgs = { contact.getNumber().flatten() };
        Cursor cursor = resolver.query(Telephony.Sms.CONTENT_URI, null, selection, selectionArgs, Telephony.Sms.DEFAULT_SORT_ORDER);
        if (cursor.getCount() == 0) {
            uiHandler.enqueue(new Runnable() {
                @Override
                public void run() {
                    hasConversationListener.noConversationForNumber();
                }
            });
        } else {
            cursor.moveToFirst();
            final int threadId = CursorHelper.asInt(cursor, Telephony.Sms.THREAD_ID);
            uiHandler.enqueue(new Runnable() {
                @Override
                public void run() {
                    hasConversationListener.hasConversation(contact, threadId);
                }
            });
        }
        cursor.close();
        return null;
    }
}

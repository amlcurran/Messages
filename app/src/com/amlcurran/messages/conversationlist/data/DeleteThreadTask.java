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
import android.provider.Telephony;

import com.amlcurran.messages.core.conversationlist.ConversationList;
import com.amlcurran.messages.core.conversationlist.Conversation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class DeleteThreadTask implements Callable<Object> {
    private final ContentResolver contentResolver;
    private final List<Conversation> conversationsToDelete;
    private final ConversationList conversationList;

    public DeleteThreadTask(ContentResolver contentResolver, List<Conversation> conversationsToDelete, ConversationList conversationList) {
        this.contentResolver = contentResolver;
        this.conversationsToDelete = conversationsToDelete;
        this.conversationList = conversationList;
    }

    @Override
    public Object call() throws Exception {
        String where = Telephony.Sms.THREAD_ID + "=?";
        List<Conversation> deletedConversations = new ArrayList<>();
        for (Conversation conversation : conversationsToDelete) {
            String[] args = new String[]{conversation.getThreadId()};
            int result = contentResolver.delete(Telephony.Sms.CONTENT_URI, where, args);
            if (result > 0) {
                deletedConversations.add(conversation);
            }
        }
        conversationList.deletedConversations(deletedConversations);
        return null;
    }
}

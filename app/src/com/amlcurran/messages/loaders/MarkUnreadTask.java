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
import android.content.ContentValues;
import android.provider.Telephony;

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.core.conversationlist.ConversationList;
import com.amlcurran.messages.core.data.SmsMessage;

import java.util.List;
import java.util.concurrent.Callable;

class MarkUnreadTask implements Callable<Object> {
    private final ContentResolver contentResolver;
    private final ConversationList conversationList;
    private final List<String> threadIds;
    private final ThreadLoader threadLoader;

    public MarkUnreadTask(ContentResolver contentResolver, ConversationList conversationList, List<String> threadIds) {
        this.contentResolver = contentResolver;
        this.conversationList = conversationList;
        this.threadIds = threadIds;
        this.threadLoader = new ThreadLoader(Telephony.Sms.Inbox.CONTENT_URI, contentResolver);
    }

    @Override
    public Object call() throws Exception {
        for (String threadId : threadIds) {
            List<SmsMessage> messageList = threadLoader.loadSmsList(threadId);
            SmsMessage lastMessage = messageList.get(messageList.size() - 1);

            // This updates an unread message
            String selection = createSelection(threadId);
            String[] args = createSelectionArgs(threadId, lastMessage);
            int updated = contentResolver.update(Telephony.Sms.Inbox.CONTENT_URI, createUnreadContentValues(), selection, args);
            if (updated == 0) {
                MessagesLog.w(MarkUnreadTask.this, "Couldn't mark conversation " + threadId + " as read");
            } else {
                conversationList.markedUnread(threadId);
            }

        }
        return null;
    }

    private static String[] createSelectionArgs(String threadId, SmsMessage lastMessage) {
        String lastMessageId = String.valueOf(lastMessage.getId());
        if (threadId != null) {
            return new String[]{threadId, lastMessageId};
        } else {
            return new String[]{lastMessageId};
        }
    }

    private static String createSelection(String threadId) {
        if (threadId != null) {
            return String.format("%1$s=? AND %2$s=?", Telephony.Sms.THREAD_ID, Telephony.Sms._ID);
        } else {
            return String.format("%1$s IS NULL AND %2$s=?", Telephony.Sms.THREAD_ID, Telephony.Sms._ID);
        }
    }

    private ContentValues createUnreadContentValues() {
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.READ, "0");
        return values;
    }
}

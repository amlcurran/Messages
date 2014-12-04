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
import android.os.Handler;
import android.provider.Telephony;

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.loaders.ConversationListChangeListener;
import com.amlcurran.messages.core.loaders.ThreadListener;

import java.util.List;
import java.util.concurrent.Callable;

class MarkUnreadTask implements Callable<Object> {
    private final ContentResolver contentResolver;
    private final List<String> threadIds;
    private final ConversationListChangeListener changeListener;
    private final Handler uiHandler;

    public MarkUnreadTask(ContentResolver contentResolver, List<String> threadIds, ConversationListChangeListener changeListener, Handler uiHandler) {
        this.contentResolver = contentResolver;
        this.threadIds = threadIds;
        this.changeListener = changeListener;
        this.uiHandler = uiHandler;
    }

    @Override
    public Object call() throws Exception {
        for (final String threadId : this.threadIds) {
            new InboxThreadTask(contentResolver, threadId, new ThreadListener() {

                @Override
                public void onThreadLoaded(List<SmsMessage> messageList) {
                    SmsMessage lastMessage = messageList.get(messageList.size() - 1);

                    // This updates an unread message
                    String selection = String.format("%1$s=? AND %2$s=?", Telephony.Sms.THREAD_ID, Telephony.Sms._ID);
                    String[] args = new String[]{threadId, String.valueOf(lastMessage.getId()) };
                    int updated = contentResolver.update(Telephony.Sms.Inbox.CONTENT_URI, createUnreadContentValues(), selection, args);
                    if (updated == 0) {
                        MessagesLog.w(MarkUnreadTask.this, "Couldn't mark conversation " + threadId + " as read");
                    }

                }
            }, uiHandler).call();
        }
        changeListener.listChanged();
        return null;
    }

    private ContentValues createUnreadContentValues() {
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.READ, "0");
        return values;
    }
}

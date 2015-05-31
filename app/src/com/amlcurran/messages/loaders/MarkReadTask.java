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

import com.amlcurran.messages.core.conversationlist.ConversationList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class MarkReadTask implements Callable<Object> {
    private final ContentResolver contentResolver;
    private final ConversationList conversationList;
    private final List<String> threadIds;
    private final ContentValues readContentValues;

    public MarkReadTask(ContentResolver contentResolver, ConversationList conversationList, List<String> threadIds) {
        this(contentResolver, conversationList, threadIds, createReadContentValues());
    }

    MarkReadTask(ContentResolver contentResolver, ConversationList conversationList, List<String> threadIds, ContentValues readContentValues) {
        this.contentResolver = contentResolver;
        this.conversationList = conversationList;
        this.threadIds = new ArrayList<>(threadIds);
        this.readContentValues = readContentValues;
    }

    @Override
    public Object call() throws Exception {
        boolean shouldUpdate = false;
        for (String threadId : threadIds) {
            String selection = createSelection(threadId);
            String[] args = createSelectionArgs(threadId);
            int update = contentResolver.update(Telephony.Sms.CONTENT_URI, readContentValues, selection, args);
            shouldUpdate = shouldUpdate || update > 0;
        }
        if (shouldUpdate) {
            conversationList.reloadConversations();
        }
        return null;
    }

    private static String createSelection(String threadId) {
        if (threadId != null) {
            return String.format("%1$s=? AND (%2$s=? OR %3$s=?)", Telephony.Sms.THREAD_ID, Telephony.Sms.READ, Telephony.Sms.SEEN);
        } else {
            return String.format("%1$s IS NULL AND (%2$s=? OR %3$s=?)", Telephony.Sms.THREAD_ID, Telephony.Sms.READ, Telephony.Sms.SEEN);
        }
    }

    private static String[] createSelectionArgs(String threadId) {
        if (threadId != null) {
            return new String[]{threadId, "0", "0"};
        } else {
            return new String[]{"0", "0"};
        }
    }

    private static ContentValues createReadContentValues() {
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.READ, "1");
        values.put(Telephony.Sms.SEEN, "1");
        return values;
    }

}

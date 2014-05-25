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
import android.database.Cursor;
import android.provider.Telephony;

import com.espian.utils.CursorHelper;

import java.util.concurrent.Callable;

class MarkUnreadTask implements Callable<Object>, CursorLoadListener {
    private final ContentResolver contentResolver;
    private final String threadId;

    public MarkUnreadTask(ContentResolver contentResolver, String threadId) {
        this.contentResolver = contentResolver;
        this.threadId = threadId;
    }

    @Override
    public Object call() throws Exception {
        new InboxThreadTask(contentResolver, threadId, this).call();
        return null;
    }

    @Override
    public void onCursorLoaded(Cursor cursor) {
        if (cursor.moveToLast()) {

            // This updates an unread message
            String selection = String.format("%1$s=? AND %2$s=?", Telephony.Sms.THREAD_ID, Telephony.Sms._ID);
            String[] args = new String[]{threadId, CursorHelper.asString(cursor, Telephony.Sms._ID)};
            contentResolver.update(Telephony.Sms.Inbox.CONTENT_URI, createUnreadContentValues(), selection, args);

        }
    }

    private ContentValues createUnreadContentValues() {
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.READ, "0");
        return values;
    }
}

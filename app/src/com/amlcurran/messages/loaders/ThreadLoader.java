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
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.data.InFlightSmsMessageFactory;

import java.util.ArrayList;
import java.util.List;

public class ThreadLoader {
    private static final String[] PROJECTION = new String[] {Telephony.Sms.BODY, Telephony.Sms._ID, Telephony.Sms.ADDRESS,
            Telephony.Sms.TYPE, Telephony.Sms.DATE, Telephony.Sms.THREAD_ID };
    private final Uri contentUri;
    private final ContentResolver contentResolver;

    public ThreadLoader(Uri contentUri, ContentResolver contentResolver) {
        this.contentUri = contentUri;
        this.contentResolver = contentResolver;
    }

    List<SmsMessage> loadSmsList(String threadId) {
        String selection = createSelectionStringFor(threadId);
        String[] selectionArgs = createSelectArgs(threadId);
        Cursor cursor = contentResolver.query(contentUri, PROJECTION, selection, selectionArgs, Telephony.Sms.DEFAULT_SORT_ORDER.replace("DESC", "ASC"));
        List<SmsMessage> messageList = createMessageList(cursor);
        cursor.close();
        return messageList;
    }

    private static String[] createSelectArgs(String threadId) {
        String[] selectionArgs;
        if (threadId != null) {
            selectionArgs = new String[]{threadId};
        } else {
            selectionArgs = null;
        }
        return selectionArgs;
    }

    private static String createSelectionStringFor(String threadId) {
        String selection;
        if (threadId != null) {
            selection = Telephony.Sms.THREAD_ID + "=?";
        } else {
            selection = "thread_id is NULL";
        }
        return selection;
    }

    private List<SmsMessage> createMessageList(Cursor cursor) {
        List<SmsMessage> messageList = new ArrayList<SmsMessage>();
        SmsMessage tempPointer;
        while (cursor.moveToNext()) {
            tempPointer = InFlightSmsMessageFactory.fromCursor(cursor);
            if (tempPointer != null) {
                messageList.add(tempPointer);
            }
        }
        return messageList;
    }
}
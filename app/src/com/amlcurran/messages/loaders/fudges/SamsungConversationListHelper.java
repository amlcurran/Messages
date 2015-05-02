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

package com.amlcurran.messages.loaders.fudges;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.amlcurran.sourcebinder.CursorHelper;

class SamsungConversationListHelper implements ConversationListHelper {
    @Override
    public Cursor queryConversationList(ContentResolver contentResolver, String query, String[] args, String sortString) {
        return contentResolver.query(Telephony.Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build(), null, query, args, sortString);
    }

    @Override
    public String getThreadIdCursorKey() {
        return Telephony.Sms.Conversations._ID;
    }

    @Override
    public String getAddressFromRow(ContentResolver contentResolver, Cursor conversationsList) {
        String recipientId = CursorHelper.asString(conversationsList, "recipient_ids");
        Cursor cursor = contentResolver.query(Uri.parse("content://mms-sms/canonical-addresses"), null, "_id = " + recipientId, null, null);
        if (cursor.moveToFirst()) {
            return CursorHelper.asString(cursor, "address");
        }
        cursor.close();
        throw new IllegalArgumentException("Thread has recipient id but no associated address");
    }

    @Override
    public String getSnippetCursorKey() {
        return Telephony.Sms.Conversations.SNIPPET;
    }

    @Override
    public int getConversationCount(ContentResolver contentResolver, String threadId) {
//        Cursor query = contentResolver.query(Telephony.Sms.CONTENT_URI, null, getThreadIdCursorKey() + "=?",
//                new String[] { threadId }, null);
//        int count = query.getCount();
//        query.close();
        return 0;
    }

    @Override
    public String getDateSentKey() {
        return Telephony.Sms.DATE;
    }

    @Override
    public String getTypeKey() {
        return "message_type";
    }
}

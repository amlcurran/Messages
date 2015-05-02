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
import android.provider.Telephony;

import com.amlcurran.sourcebinder.CursorHelper;

class ActualAndroidApiConversationListHelper implements ConversationListHelper {

    private final String[] PROJECTION;

    public ActualAndroidApiConversationListHelper() {
        PROJECTION = new String[] {
                getThreadIdCursorKey(), getSnippetCursorKey(),
                Telephony.Sms.Inbox.READ, Telephony.Sms.Inbox.ADDRESS,
                Telephony.Sms.TYPE, Telephony.Sms.DATE_SENT
        };
    }


    @Override
    public Cursor queryConversationList(ContentResolver contentResolver, String query, String[] args, String sortString) {
        return contentResolver.query(Telephony.Threads.CONTENT_URI, PROJECTION, query, args, sortString);
    }

    @Override
    public String getThreadIdCursorKey() {
        return Telephony.Sms.Conversations.THREAD_ID;
    }

    @Override
    public String getAddressFromRow(ContentResolver contentResolver, Cursor conversationsList) {
        return CursorHelper.asString(conversationsList, Telephony.Sms.ADDRESS);
    }

    @Override
    public String getSnippetCursorKey() {
        return Telephony.Sms.BODY;
    }

    @Override
    public int getConversationCount(ContentResolver contentResolver, String threadId) {
//        Cursor query = contentResolver.query(Telephony.Sms.CONTENT_URI, new String[] { getThreadIdCursorKey() }, getThreadIdCursorKey() + "=?",
//                new String[] { threadId }, null);
//        int count = query.getCount();
//        query.close();
        return 0;
    }

    @Override
    public String getDateSentKey() {
        return Telephony.Sms.DATE_SENT;
    }

    @Override
    public String getTypeKey() {
        return Telephony.Sms.TYPE;
    }
}

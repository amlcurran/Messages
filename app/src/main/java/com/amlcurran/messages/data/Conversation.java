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

package com.amlcurran.messages.data;

import android.database.Cursor;
import android.provider.Telephony;
import android.text.TextUtils;

import com.espian.utils.data.CursorHelper;

public class Conversation {

    private String address;
    private String body;
    private String threadId;
    private boolean isRead;
    private String name;
    private long personId;

    public static Conversation fromCursor(Cursor cursor, long personId, String personName) {
        Conversation conversation = new Conversation();
        conversation.address = CursorHelper.asString(cursor, Telephony.Sms.ADDRESS);
        conversation.body = CursorHelper.asString(cursor, Telephony.Sms.BODY);
        String s = CursorHelper.asString(cursor, Telephony.Sms.Inbox.READ);
        conversation.isRead = s.toLowerCase().equals("1");
        conversation.threadId = CursorHelper.asString(cursor, Telephony.Sms.THREAD_ID);
        if (TextUtils.isEmpty(personName)) {
            conversation.name = conversation.address;
        } else {
            conversation.name = personName;
        }
        conversation.personId = personId;
        return conversation;
    }

    public String getBody() {
        return body;
    }

    public String getAddress() {
        return address;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getThreadId() {
        return threadId;
    }

    public String getName() {
        return name;
    }

    public long getPersonId() {
        return personId;
    }

    @Override
    public int hashCode() {
        return threadId.hashCode();
    }
}

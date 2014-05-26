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

import com.espian.utils.data.CursorHelper;

public class Conversation {

    private final String address;
    private final String body;
    private final String threadId;
    private final boolean isRead;
    private final Contact contact;

    private Conversation(String address, String body, String threadId, boolean isRead, Contact contact) {
        this.address = address;
        this.body = body;
        this.threadId = threadId;
        this.isRead = isRead;
        this.contact = contact;
    }

    public static Conversation fromCursor(Cursor cursor, Contact contact) {
        String address = CursorHelper.asString(cursor, Telephony.Sms.ADDRESS);
        String body = CursorHelper.asString(cursor, Telephony.Sms.BODY);
        String s = CursorHelper.asString(cursor, Telephony.Sms.Inbox.READ);
        boolean isRead = s.toLowerCase().equals("1");
        String threadId = CursorHelper.asString(cursor, Telephony.Sms.THREAD_ID);
        return new Conversation(address, body, threadId, isRead, contact);
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

    @Override
    public int hashCode() {
        return threadId.hashCode();
    }

    public Contact getContact() {
        return contact;
    }
}

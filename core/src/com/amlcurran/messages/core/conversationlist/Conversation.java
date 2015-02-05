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

package com.amlcurran.messages.core.conversationlist;

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.Time;

public class Conversation {

    private final PhoneNumber address;
    private final String body;
    private final String threadId;
    boolean isRead;
    private final Contact contact;
    private final boolean lastFromMe;
    private final Time time;
    private int conversationCount;

    public Conversation(PhoneNumber address, String body, String threadId, boolean isRead, Contact contact, boolean lastFromMe, Time time, int conversationCount) {
        this.address = address;
        this.body = body;
        this.threadId = threadId;
        this.isRead = isRead;
        this.contact = contact;
        this.lastFromMe = lastFromMe;
        this.time = time;
        this.conversationCount = conversationCount;
    }

    public boolean isLastFromMe() {
        return lastFromMe;
    }

    public String getSummaryText() {
        return body;
    }

    public PhoneNumber getAddress() {
        return address;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getThreadId() {
        return threadId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Conversation) {
            if (threadId != null) {
                return threadId.equals(((Conversation) obj).getThreadId());
            } else if (((Conversation) obj).getThreadId() == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return threadId == null ? 0 : threadId.hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(threadId);
    }

    public Contact getContact() {
        return contact;
    }

    public Time getTimeOfLastMessage() {
        return time;
    }

    public int getConversationCount() {
        return conversationCount;
    }
}

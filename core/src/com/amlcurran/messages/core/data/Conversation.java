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

package com.amlcurran.messages.core.data;

public class Conversation {

    private final String address;
    private final String body;
    private final String threadId;
    private final boolean isRead;
    private final Contact contact;

    public Conversation(String address, String body, String threadId, boolean isRead, Contact contact) {
        this.address = address;
        this.body = body;
        this.threadId = threadId;
        this.isRead = isRead;
        this.contact = contact;
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

    @Override
    public String toString() {
        return String.valueOf(threadId);
    }

    public Contact getContact() {
        return contact;
    }
}

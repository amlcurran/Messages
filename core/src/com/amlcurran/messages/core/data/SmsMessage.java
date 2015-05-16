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

import java.io.Serializable;

public class SmsMessage implements Serializable {

    private final String threadId;
    private final PhoneNumber address;
    private final String body;
    private final Time timestamp;
    private final Type type;
    private final long id;

    public SmsMessage(long id, String threadId, PhoneNumber address, String body, Time timestamp, Type type) {
        this.id = id;
        this.threadId = threadId;
        this.address = address;
        this.body = body;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public PhoneNumber getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public Time getTimestamp() {
        return timestamp;
    }

    public boolean isFromMe() {
        return type != Type.INBOX;
    }

    public boolean isSending() {
        return type == Type.SENDING;
    }

    public long getId() {
        return id;
    }

    public String getThreadId() {
        return threadId;
    }

    public SmsMessage changeTypeTo(Type type) {
        return new SmsMessage(id, threadId, address, body, timestamp, type);
    }

    public enum Type {
        INBOX, SENDING, DRAFT, FAILED, UNKNOWN, SENT
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SmsMessage && id == ((SmsMessage) obj).id;
    }
}

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

public class SmsMessage implements CoreMessage {

    private final String address;
    private final String body;
    private final long timestamp;
    private final boolean isFromMe;
    private final boolean isSending;
    private final long id;

    public SmsMessage(long id, String address, String body, long timestamp, boolean isFromMe, boolean isSending) {
        this.id = id;
        this.address = address;
        this.body = body;
        this.timestamp = timestamp;
        this.isFromMe = isFromMe;
        this.isSending = isSending;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public boolean isFromMe() {
        return isFromMe;
    }

    @Override
    public String toString() {
        return String.format("SMS with address: %1$s\ntimestamp: %2$d\nbody: %3$s", address, timestamp, body);
    }

    public boolean isSending() {
        return isSending;
    }

    public long getId() {
        return id;
    }
}

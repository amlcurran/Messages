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

package com.amlcurran.messages.core.events;

import com.amlcurran.messages.core.data.PhoneNumber;

@Deprecated
public interface EventBus {

    public static final String BASE_BROADCAST = "com.amlcurran.messages";
    public static final String BROADCAST_MESSAGE_SENT = BASE_BROADCAST + ".broadcast_message_sent";
    public static final String BROADCAST_LIST_INVALIDATED = BASE_BROADCAST + ".BROADCAST_LIST_INVALIDATED";
    public static final String BROADCAST_MESSAGE_RECEIVED = BASE_BROADCAST + ".broadcast_message_received";
    public static final String BROADCAST_MESSAGE_SENDING = BASE_BROADCAST + ".broadcast_message_sending";
    public static final String BROADCAST_LIST_LOADED = BASE_BROADCAST + ".LIST_LOADED";
    public static final String BROADCAST_MESSAGE_DRAFT = BASE_BROADCAST + ".broadcast_message_drafted";

    void postMessageSent(PhoneNumber phoneNumber);

    void postMessageReceived(PhoneNumber phoneNumber);

    void postListLoaded();

    void postMessageDrafted(PhoneNumber phoneNumber);
}

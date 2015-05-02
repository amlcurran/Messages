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

package com.amlcurran.messages.core;

import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.threads.InFlightSmsMessage;
import com.amlcurran.messages.core.threads.MessagePersister;

import java.util.HashMap;
import java.util.Map;

class System {

    private final MessagePersister messagePersister;
    private final Map<PhoneNumber, ThreadListener> listenerMap;

    public System(MessagePersister messagePersister) {
        this.messagePersister = messagePersister;
        this.listenerMap = new HashMap<>();
    }

    public void receivedMessage(InFlightSmsMessage message) {
        messagePersister.writeIncomingMessage(message, persisterCallbacks);
    }

    public void listenTo(PhoneNumber phoneNumber, ThreadListener threadListener) {
        listenerMap.put(phoneNumber, threadListener);
    }

    private MessagePersister.Callbacks persisterCallbacks = new MessagePersister.Callbacks() {
        @Override
        public void newMessage(SmsMessage message) {
            if (availableListener(message.getAddress()) != null) {
                availableListener(message.getAddress()).newMessage(message);
            }
        }

        @Override
        public void writeFailed() {

        }
    };

    private ThreadListener availableListener(PhoneNumber address) {
        return listenerMap.get(address); //listenerMap.get(address) != null ? listenerMap.get(address).get() : null;
    }

}

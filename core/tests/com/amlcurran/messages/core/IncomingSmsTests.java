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
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.core.threads.InFlightSmsMessage;
import com.amlcurran.messages.core.threads.MessagePersister;
import com.amlcurran.messages.core.threads.ResultCallback;

import org.junit.Test;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class IncomingSmsTests {

    private InFlightSmsMessage writtenMessage;

    @Test
    public void testAnIncomingMessageIsSavedToThePersister() {
        InFlightSmsMessage message = incomingMessage();

        new System(new TestPersister()).receivedMessage(message);

        assertThat(writtenMessage, is(message));
    }

    @Test
    public void testWhenAnIncomingMessageIsWritten_ListenersAreNotified() {
        InFlightSmsMessage message = incomingMessage();
        System system = new System(new WritingPersister());
        CapturingThreadListener capturingThreadListener = new CapturingThreadListener();
        system.listenTo(new BasicPhoneNumber("0800 289492"), capturingThreadListener);

        system.receivedMessage(message);

        assertThat(capturingThreadListener.lastMessage, is(SmsMessage.fromInFlight(14, message)));
    }

    @Test
    public void testWhenAnIncomingMessageIsWritten_OtherListenersAreNotNotified() {
        InFlightSmsMessage message = incomingMessage();
        System system = new System(new WritingPersister());
        CapturingThreadListener capturingThreadListener = new CapturingThreadListener();
        system.listenTo(new BasicPhoneNumber("08001 289492"), capturingThreadListener);

        system.receivedMessage(message);

        assertNull(capturingThreadListener.lastMessage);
    }

    private static class System {

        private final MessagePersister messagePersister;
        private final Map<String, WeakReference<CapturingThreadListener>> listenerMap;

        public System(MessagePersister messagePersister) {
            this.messagePersister = messagePersister;
            this.listenerMap = new HashMap<>();
        }

        public void receivedMessage(InFlightSmsMessage message) {
            messagePersister.writeIncomingMessage(message, persisterCallbacks);
        }

        public void listenTo(PhoneNumber phoneNumber, CapturingThreadListener capturingThreadListener) {
            listenerMap.put(phoneNumber.flatten(), new WeakReference<>(capturingThreadListener));
        }

        private MessagePersister.Callbacks persisterCallbacks = new MessagePersister.Callbacks() {
            @Override
            public void newMessage(SmsMessage message) {
                if (availableListener(message.getAddress()) != null) {
                    availableListener(message.getAddress()).newMessage(message);
                }
            }
        };

        private CapturingThreadListener availableListener(String address) {
            return listenerMap.get(address) != null ? listenerMap.get(address).get() : null;
        }

    }

    private static InFlightSmsMessage incomingMessage() {
        return new InFlightSmsMessage("Some sms text", new BasicPhoneNumber("0800 289492"),
                SmsMessage.Type.INBOX, Time.now());
    }

    private class TestPersister implements MessagePersister {

        @Override
        public void writeMessageSending(InFlightSmsMessage message, ResultCallback<SmsMessage> resultCallback) {

        }

        @Override
        public void writeIncomingMessage(InFlightSmsMessage message, Callbacks callbacks) {
            writtenMessage = message;
        }


    }

    private class CapturingThreadListener {
        public SmsMessage lastMessage;

        public void newMessage(SmsMessage message) {
            lastMessage = message;
        }
    }

    private class WritingPersister implements MessagePersister {
        @Override
        public void writeMessageSending(InFlightSmsMessage message, ResultCallback<SmsMessage> resultCallback) {

        }

        @Override
        public void writeIncomingMessage(InFlightSmsMessage message, Callbacks callbacks) {
            callbacks.newMessage(SmsMessage.fromInFlight(14, message));
        }

    }
}

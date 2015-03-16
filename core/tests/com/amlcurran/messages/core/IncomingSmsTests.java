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

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.core.threads.InFlightSmsMessage;
import com.amlcurran.messages.core.threads.MessagePersister;
import com.amlcurran.messages.core.threads.ResultCallback;

import org.junit.Test;

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

    @Test
    public void testWhenAnIncomingMessageFailsToWrite_ListenersAreNotNotified() {
        InFlightSmsMessage message = incomingMessage();
        System system = new System(new FailWritingPersister());
        CapturingThreadListener capturingThreadListener = new CapturingThreadListener();
        system.listenTo(new BasicPhoneNumber("0800 289492"), capturingThreadListener);

        system.receivedMessage(message);

        assertNull(capturingThreadListener.lastMessage);
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

    private class CapturingThreadListener implements ThreadListener {
        public SmsMessage lastMessage;

        @Override
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

    private class FailWritingPersister implements MessagePersister {
        @Override
        public void writeMessageSending(InFlightSmsMessage message, ResultCallback<SmsMessage> resultCallback) {

        }

        @Override
        public void writeIncomingMessage(InFlightSmsMessage message, Callbacks callbacks) {
            callbacks.writeFailed();
        }
    }
}

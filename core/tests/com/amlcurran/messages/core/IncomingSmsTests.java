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
import static org.junit.Assert.assertThat;

public class IncomingSmsTests {

    private InFlightSmsMessage writtenMessage;

    @Test
    public void testAnIncomingMessageIsSavedToThePersister() {
        InFlightSmsMessage message = new InFlightSmsMessage("Some sms text", new BasicPhoneNumber("0800 289492"),
                SmsMessage.Type.INBOX, Time.now());

        new System(new TestPersister()).receivedMessage(message);

        assertThat(writtenMessage, is(message));
    }

    private static class System {

        private final MessagePersister messagePersister;

        public System(MessagePersister messagePersister) {
            this.messagePersister = messagePersister;
        }

        public void receivedMessage(InFlightSmsMessage message) {
            messagePersister.writeIncomingMessage(message);
        }
    }

    private class TestPersister implements MessagePersister {

        @Override
        public void writeMessageSending(InFlightSmsMessage message, ResultCallback<SmsMessage> resultCallback) {

        }

        @Override
        public void writeIncomingMessage(InFlightSmsMessage message) {
            writtenMessage = message;
        }


    }
}

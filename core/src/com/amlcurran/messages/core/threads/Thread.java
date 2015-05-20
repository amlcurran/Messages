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

package com.amlcurran.messages.core.threads;

import com.amlcurran.messages.core.Log;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.events.Broadcast;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.core.events.EventSubscriber;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.core.loaders.ThreadListener;

import java.util.List;

public class Thread {

    private final MessagesLoader messagesLoader;
    private final EventSubscriber messageReceiver;
    private final PhoneNumber number;
    private final String threadId;
    private ThreadCallbacks callbacks;
    private MessageTransport messageTransport;

    public Thread(MessagesLoader messagesLoader, EventSubscriber messageReceiver, PhoneNumber number, String threadId, MessageTransport messageTransport) {
        this.messagesLoader = messagesLoader;
        this.messageReceiver = messageReceiver;
        this.number = number;
        this.threadId = threadId;
        this.messageTransport = messageTransport;
    }

    public void setCallbacks(final ThreadCallbacks callbacks) {
        this.callbacks = callbacks;
        messageTransport.listenToThread(threadId, new MessageTransport.TransportCallbacks() {

            @Override
            public void messageSending(SmsMessage message) {
                callbacks.messageAdded(message);
            }

            @Override
            public void messageSent(SmsMessage message) {
                callbacks.messageChanged(message);
            }

            @Override
            public void messageReceived(SmsMessage message) {
                callbacks.messageAdded(message);
            }
        });
        messageReceiver.startListening(new LoadThreadOnMessage(), getBroadcastsToListenTo());
    }

    public void unsetCallbacks() {
        messageTransport.stopListeningToThread(threadId);
        this.callbacks = ThreadCallbacks.NULL_IMPL;
        messageReceiver.stopListening();
    }

    public void load() {
        messagesLoader.loadThread(threadId, new ThreadListener() {
            @Override
            public void onThreadLoaded(List<SmsMessage> messageList) {
                callbacks.threadLoaded(messageList);
            }
        });
    }

    private Broadcast[] getBroadcastsToListenTo() {
        String phoneNumber = number.flatten();
        return new Broadcast[]{
                new Broadcast(EventBus.BROADCAST_MESSAGE_DRAFT, phoneNumber)};
    }

    public String getId() {
        return threadId;
    }

    public void sendMessage(CharSequence messageBody) {
        final InFlightSmsMessage message = InFlightSmsMessage.timestampedNow(messageBody, number, SmsMessage.Type.SENDING);
        messageTransport.sendFromThread(threadId, message);
    }

    private class LoadThreadOnMessage implements EventSubscriber.Listener {
        @Override
        public void onMessageReceived(Broadcast broadcast) {
            switch (broadcast.getAction()) {

                case EventBus.BROADCAST_MESSAGE_RECEIVED:
                    Log.getLogger().d(this, "Message received");
                    break;

                case EventBus.BROADCAST_MESSAGE_SENDING:
                    Log.getLogger().d(this, "Message sending");
                    break;

            }
            load();
        }
    }

    public interface ThreadCallbacks {

        ThreadCallbacks NULL_IMPL = new ThreadCallbacks() {

            @Override
            public void threadLoaded(List<SmsMessage> messageList) {

            }

            @Override
            public void messageAdded(SmsMessage message) {

            }

            @Override
            public void messageChanged(SmsMessage message) {

            }
        };

        void threadLoaded(List<SmsMessage> messageList);

        void messageAdded(SmsMessage message);

        void messageChanged(SmsMessage message);
    }

}

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

    public Thread(MessagesLoader messagesLoader, EventSubscriber messageReceiver, PhoneNumber number, String threadId) {
        this.messagesLoader = messagesLoader;
        this.messageReceiver = messageReceiver;
        this.number = number;
        this.threadId = threadId;
    }

    public void setCallbacks(ThreadCallbacks callbacks) {
        this.callbacks = callbacks;
        messageReceiver.startListening(new LoadThreadOnMessage(), getBroadcastsToListenTo());
    }

    public void unsetCallbacks() {
        messageReceiver.stopListening();
        this.callbacks = ThreadCallbacks.NULL_IMPL;
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
                new Broadcast(EventBus.BROADCAST_MESSAGE_SENT, phoneNumber),
                new Broadcast(EventBus.BROADCAST_MESSAGE_RECEIVED, phoneNumber),
                new Broadcast(EventBus.BROADCAST_MESSAGE_SENDING, phoneNumber),
                new Broadcast(EventBus.BROADCAST_MESSAGE_DRAFT, phoneNumber)};
    }

    private class LoadThreadOnMessage implements EventSubscriber.Listener {
        @Override
        public void onMessageReceived() {
            load();
        }
    }

    public interface ThreadCallbacks {

        ThreadCallbacks NULL_IMPL = new ThreadCallbacks() {

            @Override
            public void threadLoaded(List<SmsMessage> messageList) {

            }
        };

        void threadLoaded(List<SmsMessage> messageList);
    }

}

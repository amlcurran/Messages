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

package com.amlcurran.messages.threads;

import android.app.Activity;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.events.Broadcast;
import com.amlcurran.messages.core.events.EventSubscriber;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.data.PhoneNumber;
import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.events.BroadcastEventSubscriber;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.ArrayListSource;
import com.github.amlcurran.sourcebinder.Source;

import java.util.List;

class ThreadController implements ThreadListener {

    private final String threadId;
    private PhoneNumber phoneNumber;
    private final Callback callback;
    private ArrayListSource<SmsMessage> source;
    private EventSubscriber messageReceiver;
    private DefaultAppChecker defaultChecker;
    private MessagesLoader messageLoader;

    public ThreadController(String threadId, PhoneNumber phoneNumber, Callback callback) {
        this.threadId = threadId;
        this.phoneNumber = phoneNumber;
        this.callback = callback;
    }

    void create(Activity activity, DefaultAppChecker.Callback callback1) {
        messageLoader = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class).get(activity).getMessagesLoader();
        messageReceiver = new BroadcastEventSubscriber(activity, new BroadcastEventSubscriber.Listener() {
            @Override
            public void onMessageReceived() {
                loadData(messageLoader, true);
            }
        });
        source = new ArrayListSource<SmsMessage>();
        defaultChecker = new DefaultAppChecker(activity, callback1);
    }

    void resume() {
        defaultChecker.checkSmsApp();
    }

    void start() {
        loadData(messageLoader, false);
        messageReceiver.startListening(getBroadcastsToListenTo());
    }

    void stop() {
        messageReceiver.stopListening();
    }

    private void loadData(MessagesLoader loader, boolean isRefresh) {
        loader.loadThread(threadId, this);
    }

    @Override
    public void onThreadLoaded(List<SmsMessage> messageList) {
        dataLoaded(messageList);
    }

    private void dataLoaded(List<SmsMessage> messageList) {
        source.replace(messageList);
        callback.dataLoaded(source);
        messageLoader.markThreadAsRead(threadId, null);
    }

    public ArrayListSource<SmsMessage> getSource() {
        return source;
    }

    private Broadcast[] getBroadcastsToListenTo() {
        return new Broadcast[]{
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_SENT, phoneNumber.toString()),
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_RECEIVED, phoneNumber.toString()),
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_SENDING, phoneNumber.toString()),
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_DRAFT, phoneNumber.toString()) };
    }

    public interface Callback {
        void dataLoaded(Source<SmsMessage> source);
    }
}

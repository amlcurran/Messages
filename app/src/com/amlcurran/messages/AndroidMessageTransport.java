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

package com.amlcurran.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.threads.InFlightSmsMessage;
import com.amlcurran.messages.core.threads.MessageTransport;
import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.telephony.SmsManagerOutputPort;

import java.util.HashMap;
import java.util.Map;

public class AndroidMessageTransport implements MessageTransport {
    private static final String MESSAGE = "extra_message";
    private static final String BROADCAST_SENDING = BuildConfig.APPLICATION_ID + ".SENDING";
    private static final String THREAD_ID = "extra-thread-id";
    private final MessagesApp messagesApp;
    private final BroadcastReceiver handleInputs = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (BROADCAST_SENDING.equals(intent.getAction())) {
                String threadId = intent.getStringExtra(THREAD_ID);
                notifyListeners(threadId, new CallbackAction() {

                    @Override
                    public void act(TransportCallbacks callbacks) {
                        SmsMessage smsMessage = (SmsMessage) intent.getSerializableExtra(MESSAGE);
                        callbacks.messageSending(smsMessage);
                    }
                });
            }
        }
    };
    private final Map<String, TransportCallbacks> callbacksMap;
    private final BroadcastEventBus broadcastEventBus;

    public AndroidMessageTransport(MessagesApp messagesApp) {
        this.messagesApp = messagesApp;
        this.callbacksMap = new HashMap<>();
        broadcastEventBus = new BroadcastEventBus(messagesApp);
    }

    @Override
    public void sendFromThread(String threadId, final InFlightSmsMessage message) {
        com.amlcurran.messages.data.InFlightSmsMessage newInFlightSms = new com.amlcurran.messages.data.InFlightSmsMessage(message.getNumber(),
                message.getBody().toString(), message.getTimestamp());
        messagesApp.startService(SmsManagerOutputPort.sendMessageIntent(messagesApp, threadId, newInFlightSms));
    }

    private void notifyListeners(String threadId, CallbackAction callbackAction) {
        if (callbacksMap.get(threadId) != null) {
            callbackAction.act(callbacksMap.get(threadId));
        }
    }

    @Override
    public void listenToThread(String threadId, TransportCallbacks transportCallbacks) {
        callbacksMap.put(threadId, transportCallbacks);
    }

    @Override
    public void stopListeningToThread(String threadId) {
        callbacksMap.remove(threadId);
    }

    @Override
    public void start() {
        messagesApp.registerReceiver(handleInputs, transportFilter());
    }

    @Override
    public void stop() {
        messagesApp.unregisterReceiver(handleInputs);
    }

    @Override
    public void sentFromThread(final SmsMessage smsMessage) {
        notifyListeners(smsMessage.getThreadId(), new CallbackAction() {
            @Override
            public void act(TransportCallbacks callbacks) {
                callbacks.messageSent(smsMessage);
            }
        });
        broadcastEventBus.postMessageSent(smsMessage.getAddress());
    }

    @Override
    public void received(final SmsMessage smsMessage) {
        SingletonManager.getNotifier(messagesApp).addNewMessageNotification(smsMessage);
        notifyListeners(smsMessage.getThreadId(), new CallbackAction() {
            @Override
            public void act(TransportCallbacks callbacks) {
                callbacks.messageReceived(smsMessage);
            }
        });
        broadcastEventBus.postMessageReceived(smsMessage.getAddress());
    }

    @Override
    public void resendMessage(SmsMessage smsMessage) {
        messagesApp.startService(SmsManagerOutputPort.resendMessageIntent(messagesApp, smsMessage));
    }

    public static Intent sendingMessageBroadcast(Context context, String threadId, SmsMessage smsMessage) {
        return new Intent(BROADCAST_SENDING)
                .setPackage(context.getPackageName())
                .putExtra(MESSAGE, smsMessage)
                .putExtra(THREAD_ID, threadId);
    }

    private interface CallbackAction {
        void act(TransportCallbacks callbacks);
    }

    private IntentFilter transportFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_SENDING);
        return filter;
    }

}

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

import android.view.MenuItem;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.ExternalEventManager;
import com.amlcurran.messages.R;
import com.amlcurran.messages.core.TextUtils;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.data.PhoneNumberOnlyContact;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.events.Broadcast;
import com.amlcurran.messages.core.events.EventSubscriber;
import com.amlcurran.messages.core.loaders.OnContactQueryListener;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.github.amlcurran.sourcebinder.ArrayListSource;

import java.util.List;

class ThreadController implements ThreadListener {

    private final String threadId;
    private final Contact contact;
    private final ThreadView threadView;
    private final ArrayListSource<SmsMessage> source;
    private final EventSubscriber messageReceiver;
    private final DefaultAppChecker defaultChecker;
    private final DraftRepository draftRepository;
    private final ExternalEventManager externalEventManager;
    private final MessagesLoader messageLoader;

    public ThreadController(String threadId, Contact contact, String composedMessage, ThreadView threadView, EventSubscriber messageReceiver, DefaultAppChecker defaultChecker, DependencyRepository dependencyRepository) {
        this.threadId = threadId;
        this.contact = contact;
        this.threadView = threadView;
        this.messageLoader = dependencyRepository.getMessagesLoader();
        this.messageReceiver = messageReceiver;
        this.defaultChecker = defaultChecker;
        this.draftRepository = dependencyRepository.getDraftRepository();
        this.externalEventManager = dependencyRepository.getExternalEventManager();
        this.source = new ArrayListSource<SmsMessage>();
        retrieveDraft(composedMessage);
    }

    private void retrieveDraft(String composedMessage) {
        if (TextUtils.isNotEmpty(composedMessage)) {
            threadView.setComposedMessage(composedMessage);
        } else {
            threadView.setComposedMessage(draftRepository.getDraft(contact.getNumber()));
        }
    }

    void start() {
        setUpContactView(contact);
        defaultChecker.checkSmsApp(threadView);
        messageLoader.loadThread(threadId, this);
        messageReceiver.startListening(new LoadThreadOnMessage(), getBroadcastsToListenTo());
    }

    void stop() {
        messageReceiver.stopListening();
        if (TextUtils.isText(threadView.getComposedMessage())) {
            draftRepository.storeDraft(contact.getNumber(), threadView.getComposedMessage());
        }
    }

    @Override
    public void onThreadLoaded(List<SmsMessage> messageList) {
        source.replace(messageList);
        threadView.showThreadList(source.getCount());
        messageLoader.markThreadAsRead(threadId, null);
    }

    public ArrayListSource<SmsMessage> getSource() {
        return source;
    }

    private Broadcast[] getBroadcastsToListenTo() {
        String phoneNumber = contact.getNumber().flatten();
        return new Broadcast[]{
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_SENT, phoneNumber),
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_RECEIVED, phoneNumber),
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_SENDING, phoneNumber),
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_DRAFT, phoneNumber)};
    }

    private void setUpContactView(Contact contact) {
        if (contact instanceof PhoneNumberOnlyContact) {
            messageLoader.queryContact(contact.getNumber(), new OnContactQueryListener() {
                @Override
                public void contactLoaded(Contact contact) {
                    threadView.bindContactToHeader(contact);
                }
            });
        } else {
            threadView.bindContactToHeader(contact);
        }
    }

    public boolean menuItemClicked(MenuItem item) {
        if (item.getItemId() == R.id.menu_call) {
            externalEventManager.callNumber(contact.getNumber());
            return true;
        }
        return false;
    }

    public interface ThreadView extends DefaultAppChecker.Callback {
        void showThreadList(int count);

        void bindContactToHeader(Contact contact);

        String getComposedMessage();

        void setComposedMessage(String composedMessage);
    }

    private class LoadThreadOnMessage implements EventSubscriber.Listener {
        @Override
        public void onMessageReceived() {
            messageLoader.loadThread(threadId, ThreadController.this);
        }
    }
}

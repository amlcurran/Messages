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

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.ExternalEventManager;
import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumberOnlyContact;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.core.loaders.OnContactQueryListener;
import com.amlcurran.messages.core.threads.Thread;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.amlcurran.sourcebinder.source.ListSource;

import java.util.Collections;
import java.util.List;

class ThreadViewController implements ComposeMessageView.ComposureCallbacks {

    public static final int MARK_READ_DELAY = 1000;
    private final Contact contact;
    private final ThreadView threadView;
    private final ScheduledQueue scheduledQueue;
    private final ListSource<SmsMessage> source;
    private final ExternalEventManager externalEventManager;
    private final MessagesLoader messageLoader;
    private final Thread thread;

    public ThreadViewController(Thread thread, Contact contact, ThreadView threadView, DependencyRepository dependencyRepository, ScheduledQueue scheduledQueue) {
        this.contact = contact;
        this.threadView = threadView;
        this.scheduledQueue = scheduledQueue;
        this.messageLoader = dependencyRepository.getMessagesLoader();
        this.externalEventManager = dependencyRepository.getExternalEventManager();
        this.source = new ListSource<>();
        this.thread = thread;
    }

    void start() {
        setUpContactView(contact);
        thread.setCallbacks(callbacks);
        thread.load();
    }

    void stop() {
        thread.unsetCallbacks();
        scheduledQueue.removeEvents(runnable);
    }

    private Runnable runnable;
    private Thread.ThreadCallbacks callbacks = new Thread.ThreadCallbacks() {
        @Override
        public void threadLoaded(List<SmsMessage> messageList) {
            Collections.reverse(messageList);
            source.replace(messageList);
            runnable = scheduledQueue.executeWithDelay(new Runnable() {

                @Override
                public void run() {
                    messageLoader.markThreadAsRead(thread.getId());
                }
            }, MARK_READ_DELAY);
        }

        @Override
        public void messageAdded(SmsMessage message) {
            int index = source.indexOf(message);
            if (index == -1) {
                source.add(0, message);
                threadView.scrollTo(0);
            } else {
                messageChanged(message);
            }
        }

        @Override
        public void messageChanged(SmsMessage message) {
            int index = source.indexOf(message);
            source.replaceAt(index, message);
        }
    };

    public ListSource<SmsMessage> getSource() {
        return source;
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

    public boolean menuItemClicked(int itemId) {
        if (itemId == R.id.menu_call) {
            externalEventManager.callNumber(contact.getNumber());
            return true;
        } else if (itemId == R.id.modal_mark_unread) {
            messageLoader.markThreadsAsUnread(Collections.singletonList(thread.getId()));
            threadView.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onMessageComposed(CharSequence body) {
        thread.sendMessage(body);
    }

    public interface ThreadView {

        void bindContactToHeader(Contact contact);

        void finish();

        void scrollTo(int position);

    }

}

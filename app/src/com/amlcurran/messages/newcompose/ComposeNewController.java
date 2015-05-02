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

package com.amlcurran.messages.newcompose;

import android.content.res.Resources;
import android.os.Handler;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.core.conversationlist.ConversationLoader;
import com.amlcurran.messages.core.conversationlist.HasConversationListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.transition.TransitionManager;
import com.amlcurran.sourcebinder.source.ListSource;

import java.util.Calendar;
import java.util.List;

import static com.amlcurran.messages.core.TextUtils.isNotEmpty;

class ComposeNewController {
    private final ComposeNewView composeNewView;
    private final PersonPicker personPicker;
    private final SmsComposeListener smsComposeListener;
    private final DefaultAppChecker defaultAppChecker;
    private final Resources resources;
    private final ConversationLoader conversationLoader;
    private final MessagesLoader messagesLoader;
    private final TransitionManager transitionManager;
    private final ListSource<Contact> source;

    public ComposeNewController(ComposeNewView composeNewView, PersonPicker personPicker, DependencyRepository dependencyRepository, SmsComposeListener smsComposeListener, DefaultAppChecker defaultAppChecker, Resources resources, ConversationLoader conversationLoader) {
        this.composeNewView = composeNewView;
        this.personPicker = personPicker;
        this.smsComposeListener = smsComposeListener;
        this.defaultAppChecker = defaultAppChecker;
        this.resources = resources;
        this.conversationLoader = conversationLoader;
        this.messagesLoader = dependencyRepository.getMessagesLoader();
        this.transitionManager = dependencyRepository.getTransitionManager();
        this.source = new ListSource<>();
    }

    public void messageComposed(CharSequence body) {
        PhoneNumber enteredAddress = personPicker.getEnteredAddress();
        if (enteredAddress.isValid()) {
            String message = String.valueOf(body);
            long timestamp = Calendar.getInstance().getTimeInMillis();
            InFlightSmsMessage smsMessage = new InFlightSmsMessage(enteredAddress, message, Time.fromMillis(timestamp));
            smsComposeListener.sendSms(smsMessage);
        } else {
            composeNewView.sendFailedWithInvalidRecipient();
        }
    }

    public void resume() {
        defaultAppChecker.checkSmsApp(composeNewView);
    }

    public ListSource<Contact> getSource() {
        return source;
    }

    public void personSelected(int position) {
        final Contact contact = source.getAtPosition(position);
        conversationLoader.getHasConversationWith(contact, new HasConversationListener() {

            @Override
            public void noConversationForNumber() {
                personPicker.chosenRecipient(contact);

            }

            @Override
            public void hasConversation(Contact contact, int threadId) {
                transitionManager.to().thread(contact, String.valueOf(threadId), composeNewView.getComposedMessage());
            }
        });
    }

    public void create(String address, String message) {
        if (isNotEmpty(address)) {
            personPicker.setEnteredAddress(address);
        }
        if (isNotEmpty(message)) {
            composeNewView.setComposedMessage(message);
        }
        messagesLoader.loadContacts(new DelayedDataLoader());
    }

    private class ReplaceDataRunnable implements Runnable {
        private final List<Contact> contacts;

        public ReplaceDataRunnable(List<Contact> contacts) {
            this.contacts = contacts;
        }

        @Override
        public void run() {
            getSource().replace(contacts);
        }
    }

    private class DelayedDataLoader implements ContactListListener {

        @Override
        public void contactListLoaded(final List<Contact> contacts) {
            new Handler().postDelayed(new ReplaceDataRunnable(contacts), resources.getInteger(android.R.integer.config_shortAnimTime) + 100);
        }
    }
}

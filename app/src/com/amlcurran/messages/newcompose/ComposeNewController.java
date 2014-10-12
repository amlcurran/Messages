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
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.ParcelablePhoneNumber;
import com.amlcurran.messages.loaders.HasConversationListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.transition.TransitionManager;
import com.github.amlcurran.sourcebinder.ArrayListSource;

import java.util.Calendar;
import java.util.List;

public class ComposeNewController {
    private final ComposeNewView composeNewView;
    private final SmsComposeListener smsComposeListener;
    private final DefaultAppChecker defaultAppChecker;
    private final Resources resources;
    private final MessagesLoader messagesLoader;
    private final TransitionManager transitionManager;
    private final ArrayListSource<Contact> source;

    public ComposeNewController(ComposeNewView composeNewView, DependencyRepository dependencyRepository, SmsComposeListener smsComposeListener, DefaultAppChecker defaultAppChecker, Resources resources) {
        this.composeNewView = composeNewView;
        this.smsComposeListener = smsComposeListener;
        this.defaultAppChecker = defaultAppChecker;
        this.resources = resources;
        this.messagesLoader = dependencyRepository.getMessagesLoader();
        this.transitionManager = dependencyRepository.getTransitionManager();
        this.source = new ArrayListSource<Contact>();
    }

    public void onMessageComposed(CharSequence body) {
        if (isValid(composeNewView.getEnteredAddress())) {
            String address = String.valueOf(composeNewView.getEnteredAddress());
            ParcelablePhoneNumber phoneNumber = new ParcelablePhoneNumber(address);
            String message = String.valueOf(body);
            long timestamp = Calendar.getInstance().getTimeInMillis();
            InFlightSmsMessage smsMessage = new InFlightSmsMessage(phoneNumber, message, Time.fromMillis(timestamp));
            smsComposeListener.sendSms(smsMessage);
        } else {
            composeNewView.sendFailedWithInvalidRecipient();
        }
    }

    private static boolean isValid(CharSequence address) {
        return PhoneNumberUtils.isWellFormedSmsAddress(String.valueOf(address));
    }

    public void create(Bundle arguments) {
        if (hasPreparedAddress(arguments)) {
            composeNewView.setRecipient(getPreparedAddress(arguments));
        }
        if (hasPreparedMessage(arguments)) {
            composeNewView.setComposedMessage(getPreparedMessage(arguments));
        }
        messagesLoader.loadContacts(new DelayedDataLoader());
    }

    private String getPreparedMessage(Bundle arguments) {
        return arguments.getString(ComposeNewFragment.EXTRA_MESSAGE);
    }

    public String getPreparedAddress(Bundle arguments) {
        return arguments.getString(ComposeNewFragment.EXTRA_ADDRESS);
    }

    private boolean hasPreparedMessage(Bundle arguments) {
        return arguments != null && arguments.containsKey(ComposeNewFragment.EXTRA_MESSAGE);
    }

    private boolean hasPreparedAddress(Bundle arguments) {
        return arguments != null && arguments.containsKey(ComposeNewFragment.EXTRA_ADDRESS);
    }

    public void resume() {
        defaultAppChecker.checkSmsApp(composeNewView);
    }

    public ArrayListSource<Contact> getSource() {
        return source;
    }

    public void personSelected(int position) {
        final Contact contact = source.getAtPosition(position);
        messagesLoader.getHasConversationWith(contact, new HasConversationListener() {

            @Override
            public void noConversationForNumber() {
                composeNewView.chosenContact(contact);

            }

            @Override
            public void hasConversation(Contact contact, int threadId) {
                transitionManager.to().thread(contact, String.valueOf(threadId), composeNewView.getComposedMessage());
            }
        });
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

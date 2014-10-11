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

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.ParcelablePhoneNumber;
import com.amlcurran.messages.loaders.HasConversationListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.transition.TransitionManager;
import com.amlcurran.messages.ui.ComposeMessageView;

import java.util.Calendar;

public class ComposeNewController implements ChooserListener, ComposeMessageView.ComposureCallbacks {
    private final ComposeNewView composeNewView;
    private final SmsComposeListener smsComposeListener;
    private final DefaultAppChecker defaultAppChecker;
    private final MessagesLoader messagesLoader;
    private final TransitionManager transitionManager;

    public ComposeNewController(ComposeNewView composeNewView, DependencyRepository dependencyRepository, SmsComposeListener smsComposeListener, DefaultAppChecker defaultAppChecker) {
        this.composeNewView = composeNewView;
        this.smsComposeListener = smsComposeListener;
        this.defaultAppChecker = defaultAppChecker;
        this.messagesLoader = dependencyRepository.getMessagesLoader();
        this.transitionManager = dependencyRepository.getTransitionManager();
    }

    @Override
    public void recipientChosen(final Contact contact) {
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

    @Override
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
}

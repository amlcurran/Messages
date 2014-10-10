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

import android.content.ContentResolver;
import android.os.Bundle;

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.threads.ThreadFragment;
import com.amlcurran.messages.ui.control.FragmentController;

public class TransitionManager {
    private final FragmentController fragmentController;
    private final ActivityController activityController;
    private final ContentResolver contentResolver;

    public TransitionManager(FragmentController fragmentController, ActivityController activityController, ContentResolver contentResolver) {
        this.fragmentController = fragmentController;
        this.activityController = activityController;
        this.contentResolver = contentResolver;
    }

    public void callNumber(PhoneNumber phoneNumber) {
        activityController.callNumber(phoneNumber);
    }

    public void viewContact(Contact contact) {
        activityController.viewContact(ContactFactory.uriForContact(contact, contentResolver));
    }

    public void addContact(Contact contact) {
        activityController.addContact(contact.getNumber());
    }

    public void toAbout() {
        activityController.showAbout();
    }

    public void toPreferences() {
        activityController.showPreferences();
    }

    public int getView() {
        return fragmentController.getLayoutResourceId();
    }

    public boolean backPressed() {
        return fragmentController.backPressed();
    }

    public TransitionAnchor to() {
        return new DefaultTransitionAnchor();
    }

    public TransitionAnchor startAt() {
        return new DefaultTransitionAnchor();
    }

    public interface TransitionAnchor {

        TransitionManager newCompose();

        TransitionManager thread(Contact contact, String threadId, String writtenMessage);

        TransitionManager thread(PhoneNumber number, String threadId);

        TransitionManager conversationList();

        TransitionManager newComposeWithMessage(String message);

        TransitionManager newComposeWithNumber(String sendAddress);

        TransitionManager mmsError();
    }

    public class DefaultTransitionAnchor implements TransitionAnchor {

        @Override
        public TransitionManager newCompose() {
            fragmentController.loadComposeNewFragment();
            return TransitionManager.this;
        }

        @Override
        public TransitionManager thread(Contact contact, String threadId, String writtenMessage) {
            Bundle contactBundle = ContactFactory.smooshContact(contact);
            ThreadFragment fragment = ThreadFragment.create(threadId, contact.getNumber(), contactBundle, writtenMessage);
            fragmentController.replaceFragment(fragment);
            return TransitionManager.this;
        }

        @Override
        public TransitionManager thread(PhoneNumber number, String threadId) {
            ThreadFragment fragment = ThreadFragment.create(threadId, number, null, null);
            fragmentController.replaceFragment(fragment);
            return TransitionManager.this;
        }

        @Override
        public TransitionManager conversationList() {
            fragmentController.loadConversationListFragment();
            return TransitionManager.this;
        }

        @Override
        public TransitionManager newComposeWithMessage(String message) {
            fragmentController.replaceFragment(ComposeNewFragment.withMessage(message));
            return TransitionManager.this;
        }

        @Override
        public TransitionManager newComposeWithNumber(String sendAddress) {
            fragmentController.replaceFragment(ComposeNewFragment.withAddress(sendAddress));
            return TransitionManager.this;
        }

        @Override
        public TransitionManager mmsError() {
            fragmentController.replaceFragment(new MmsErrorFragment());
            return TransitionManager.this;
        }
    }

}

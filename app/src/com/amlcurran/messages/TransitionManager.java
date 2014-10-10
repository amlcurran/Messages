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

    public void toThread(Contact contact, String threadId, String writtenMessage) {
        Bundle contactBundle = ContactFactory.smooshContact(contact);
        ThreadFragment fragment = ThreadFragment.create(threadId, contact.getNumber(), contactBundle, writtenMessage);
        fragmentController.replaceFragment(fragment);
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
}

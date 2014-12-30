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

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.reporting.StatReporter;

public class ExternalEventManager {
    private final ActivityController activityController;
    private final ContentResolver contentResolver;
    private final StatReporter statReporter;

    public ExternalEventManager(ActivityController activityController, ContentResolver contentResolver, StatReporter statReporter) {
        this.activityController = activityController;
        this.contentResolver = contentResolver;
        this.statReporter = statReporter;
    }

    public void viewContact(Contact contact) {
        statReporter.sendUiEvent("view_contact");
        activityController.viewContact(ContactFactory.uriForContact(contact, contentResolver));
    }

    public void addContact(Contact contact) {
        statReporter.sendUiEvent("add_contact");
        activityController.addContact(contact.getNumber());
    }

    public void callNumber(PhoneNumber phoneNumber) {
        statReporter.sendUiEvent("call_number");
        activityController.callNumber(phoneNumber);
    }

    public void switchSmsApp() {
        activityController.switchSmsApp();
    }

    public interface Provider {
        ExternalEventManager getExternalEventManager();
    }
}

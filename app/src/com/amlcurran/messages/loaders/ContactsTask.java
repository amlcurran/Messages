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

package com.amlcurran.messages.loaders;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;

import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.data.ContactFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class ContactsTask implements Callable {
    private final ContentResolver resolver;
    private final ContactListListener contactListListener;
    private Handler uiHandler;

    public ContactsTask(ContentResolver resolver, ContactListListener contactListListener, Handler uiHandler) {
        this.resolver = resolver;
        this.contactListListener = contactListListener;
        this.uiHandler = uiHandler;
    }

    @Override
    public Object call() throws Exception {
        Cursor cursor = queryContacts();
        final List<Contact> contacts = listFromCursor(cursor);
        cursor.close();
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                contactListListener.contactListLoaded(contacts);
            }
        });
        return null;
    }

    private Cursor queryContacts() {
        String selection = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "='1'";
        String sort = ContactsContract.Data.DISPLAY_NAME_PRIMARY + " ASC";
        return resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, ContactFactory.VALID_PROJECTION, selection, null, sort);
    }

    private List<Contact> listFromCursor(Cursor cursor) {
        List<Contact> contacts = new ArrayList<Contact>();
        Contact tempPointer;
        while (cursor.moveToNext()) {
            tempPointer = ContactFactory.fromCursor(cursor);
            if (!contacts.contains(tempPointer)) {
                contacts.add(tempPointer);
            }
        }
        return contacts;
    }
}

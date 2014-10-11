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
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumberOnlyContact;
import com.amlcurran.messages.core.loaders.OnContactQueryListener;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.data.ParcelablePhoneNumber;

import java.util.concurrent.Callable;

class ContactTask implements Callable<Object> {
    private final ContentResolver contentResolver;
    private final String address;
    private final OnContactQueryListener onContactQueryListener;
    private Handler uiHandler;

    public ContactTask(ContentResolver contentResolver, String address, OnContactQueryListener onContactQueryListener, Handler uiHandler) {
        this.contentResolver = contentResolver;
        this.address = address;
        this.onContactQueryListener = onContactQueryListener;
        this.uiHandler = uiHandler;
    }

    static Uri createPhoneLookupUri(String phoneRaw) {
        return Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(phoneRaw));
    }

    @Override
    public Object call() throws Exception {
        Cursor result = contentResolver.query(createPhoneLookupUri(address), ContactFactory.VALID_PROJECTION, null, null, null);
        if (result.moveToFirst()) {
            Contact contact = ContactFactory.fromCursor(result);
            postResult(contact);
        } else {
            PhoneNumberOnlyContact contact = new PhoneNumberOnlyContact(new ParcelablePhoneNumber(address));
            postResult(contact);
        }
        result.close();
        return null;
    }

    private void postResult(final Contact contact) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                onContactQueryListener.contactLoaded(contact);
            }
        });
    }
}

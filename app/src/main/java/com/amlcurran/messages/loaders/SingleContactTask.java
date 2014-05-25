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
import android.provider.ContactsContract;

import com.amlcurran.messages.OnContactQueryListener;
import com.espian.utils.CursorHelper;

import java.util.concurrent.Callable;

class SingleContactTask implements Callable<Object> {
    private final ContentResolver contentResolver;
    private final String address;
    private final OnContactQueryListener onContactQueryListener;

    public SingleContactTask(ContentResolver contentResolver, String address, OnContactQueryListener onContactQueryListener) {
        this.contentResolver = contentResolver;
        this.address = address;
        this.onContactQueryListener = onContactQueryListener;
    }

    @Override
    public Object call() throws Exception {
        Cursor result = contentResolver.query(ExecutorMessagesLoader.createPhoneLookupUri(address), new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY },
                null, null, null);
        if (result.moveToFirst()) {
            String lookupKey = CursorHelper.asString(result, ContactsContract.Contacts.LOOKUP_KEY);
            long id = CursorHelper.asLong(result, ContactsContract.Contacts._ID);
            Uri lookupUri = ContactsContract.Contacts.getLookupUri(id, lookupKey);
            onContactQueryListener.contactLoaded(ContactsContract.Contacts.lookupContact(contentResolver, lookupUri));
        }
        return null;
    }
}

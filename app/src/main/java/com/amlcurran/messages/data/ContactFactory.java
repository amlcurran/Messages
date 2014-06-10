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

package com.amlcurran.messages.data;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.amlcurran.messages.core.data.RawContact;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.SavedContact;
import com.github.amlcurran.sourcebinder.CursorHelper;

public class ContactFactory {

    public static final String[] VALID_PROJECTION = new String[] {ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.LOOKUP_KEY };

    public static Contact fromCursor(Cursor peopleCursor) {
        String person = CursorHelper.asString(peopleCursor, ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME_PRIMARY);
        long photoId = CursorHelper.asLong(peopleCursor, ContactsContract.Contacts.PHOTO_ID);
        long contactId = CursorHelper.asLong(peopleCursor, ContactsContract.Contacts._ID);
        String rawAddress = CursorHelper.asString(peopleCursor, ContactsContract.CommonDataKinds.Phone.NUMBER);
        String lookupKey = CursorHelper.asString(peopleCursor, ContactsContract.Contacts.LOOKUP_KEY);
        return new SavedContact(contactId, person, rawAddress, photoId, lookupKey);
    }

    public static Contact fromAddress(String address1) {
        return new RawContact(address1);
    }
}

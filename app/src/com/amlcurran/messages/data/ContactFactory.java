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

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.amlcurran.messages.bucket.BundleBuilder;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumberOnlyContact;
import com.amlcurran.messages.core.data.SavedContact;
import com.amlcurran.sourcebinder.CursorHelper;

public class ContactFactory {

    public static final String[] VALID_PROJECTION = new String[] {ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.CommonDataKinds.Phone.TYPE };

    public static final String SMOOSH_IS_SAVED = "saved";
    public static final String SMOOSH_NUMBER = "number";
    public static final String SMOOSH_LOOKUP_KEY = "lookupKey";
    public static final String SMOOSH_CONTACT_ID = "contactId";
    public static final String SMOOSH_PHOTO_ID = "photoId";
    public static final String SMOOSH_DISPLAY_NAME = "displayName";
    private static final String SMOOSH_PHONE_TYPE = "phoneType";

    public static Contact fromCursor(Cursor peopleCursor) {
        String person = CursorHelper.asString(peopleCursor, ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME_PRIMARY);
        long photoId = CursorHelper.asLong(peopleCursor, ContactsContract.Contacts.PHOTO_ID);
        long contactId = CursorHelper.asLong(peopleCursor, ContactsContract.Contacts._ID);
        String rawAddress = CursorHelper.asString(peopleCursor, ContactsContract.CommonDataKinds.Phone.NUMBER);
        String lookupKey = CursorHelper.asString(peopleCursor, ContactsContract.Contacts.LOOKUP_KEY);
        int phoneType = CursorHelper.asInt(peopleCursor, ContactsContract.CommonDataKinds.Phone.TYPE);
        return new SavedContact(contactId, person, new ParcelablePhoneNumber(rawAddress), photoId, lookupKey, phoneType);
    }

    public static Contact fromAddress(String number) {
        return new PhoneNumberOnlyContact(new ParcelablePhoneNumber(number));
    }

    public static Uri uriForContact(Contact contact, ContentResolver contentResolver) {
        Uri lookupUri = ContactsContract.Contacts.getLookupUri(contact.getContactId(), contact.getLookupKey());
        return ContactsContract.Contacts.lookupContact(contentResolver, lookupUri);
    }

    public static Bundle smooshContact(Contact contact) {
        return new BundleBuilder()
                .put(SMOOSH_DISPLAY_NAME, contact.getDisplayName())
                .put(SMOOSH_PHOTO_ID, contact.getPhotoId())
                .put(SMOOSH_CONTACT_ID, contact.getContactId())
                .put(SMOOSH_LOOKUP_KEY, contact.getLookupKey())
                .put(SMOOSH_NUMBER, contact.getNumber().flatten())
                .put(SMOOSH_IS_SAVED, contact.isSaved())
                .put(SMOOSH_PHONE_TYPE, contact.getPhoneNumberType())
                .build();
    }

    public static Contact desmooshContact(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        if (bundle.getBoolean(SMOOSH_IS_SAVED)) {
            String person = bundle.getString(SMOOSH_DISPLAY_NAME);
            long photoId = bundle.getLong(SMOOSH_PHOTO_ID);
            long contactId = bundle.getLong(SMOOSH_CONTACT_ID);
            String rawAddress = bundle.getString(SMOOSH_NUMBER);
            String lookupKey = bundle.getString(SMOOSH_LOOKUP_KEY);
            int phoneType = bundle.getInt(SMOOSH_PHONE_TYPE);
            return new SavedContact(contactId, person, new ParcelablePhoneNumber(rawAddress), photoId, lookupKey, phoneType);
        } else {
            return new PhoneNumberOnlyContact(new ParcelablePhoneNumber(bundle.getString(SMOOSH_NUMBER)));
        }
    }

}

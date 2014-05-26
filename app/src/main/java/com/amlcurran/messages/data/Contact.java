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
import android.text.TextUtils;

import com.espian.utils.data.CursorHelper;

public class Contact {

    private final String name;
    private final String address;
    private final long photoId;

    public Contact(String name, String address, long photoId) {
        this.name = name;
        this.address = address;
        this.photoId = photoId;
    }

    public static Contact fromCursor(Cursor peopleCursor, String address) {
        String person = getContactName(peopleCursor);
        long photoId = getContactPhotoId(peopleCursor);
        return new Contact(person, address, photoId);
    }

    public String getDisplayName() {
        if (!TextUtils.isEmpty(name)) {
            return name;
        } else {
            return address;
        }
    }

    public long getPhotoId() {
        return photoId;
    }

    private static String getContactName(Cursor peopleCursor) {
        String result = null;
        if (peopleCursor.moveToFirst()) {
            result = CursorHelper.asString(peopleCursor, ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME_PRIMARY);
        }
        return result;
    }

    private static long getContactPhotoId(Cursor peopleCursor) {
        long id = -1;
        if (peopleCursor.moveToFirst()) {
            id = CursorHelper.asLong(peopleCursor, ContactsContract.Contacts.PHOTO_ID);
        }
        return id;
    }
}

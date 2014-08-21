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

package com.amlcurran.messages.core.data;

import com.amlcurran.messages.core.TextUtils;

public class SavedContact implements Contact {

    private final long contactId;
    private final String name;
    private final PhoneNumber address;
    private final long photoId;
    private final String lookupKey;
    private final int phoneNumberType;

    public SavedContact(long contactId, String name, PhoneNumber address, long photoId, String lookupKey, int phoneNumberType) {
        this.contactId = contactId;
        this.name = name;
        this.address = address;
        this.photoId = photoId;
        this.lookupKey = lookupKey;
        this.phoneNumberType = phoneNumberType;
    }

    @Override
    public String getDisplayName() {
        if (!TextUtils.isEmpty(name)) {
            return name;
        } else {
            return address.flatten();
        }
    }

    @Override
    public int getPhoneNumberType() {
        return phoneNumberType;
    }

    @Override
    public long getPhotoId() {
        return photoId;
    }

    @Override
    public long getContactId() {
        return contactId;
    }

    @Override
    public String getLookupKey() {
        return lookupKey;
    }

    @Override
    public PhoneNumber getNumber() {
        return address;
    }

    @Override
    public boolean isSaved() {
        return true;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(contactId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SavedContact && contactId == ((SavedContact) obj).contactId && contactId != -1;
    }
}

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

package com.amlcurran.messages.conversationlist.data;

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.data.ParcelablePhoneNumber;

public class UnknownContact implements Contact {
    @Override
    public String getDisplayName() {
        return "Unknown Contact";
    }

    @Override
    public int getPhoneNumberType() {
        return 0;
    }

    @Override
    public long getPhotoId() {
        return -1;
    }

    @Override
    public long getContactId() {
        return -1;
    }

    @Override
    public String getLookupKey() {
        return null;
    }

    @Override
    public PhoneNumber getNumber() {
        return new ParcelablePhoneNumber("");
    }

    @Override
    public boolean isSaved() {
        return false;
    }
}

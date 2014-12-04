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

import android.graphics.Bitmap;
import android.util.LruCache;

import com.amlcurran.messages.core.data.Contact;

public class MemoryMessagesCache implements MessagesCache {

    private final LruCache<Long, Bitmap> contactPhotoCache;

    public MemoryMessagesCache() {
        contactPhotoCache = new LruCache<Long, Bitmap>(20);
    }

    @Override
    public Bitmap getContactPhoto(Contact contact) {
        return contactPhotoCache.get(contact.getContactId());
    }

    @Override
    public void storeContactPhoto(Contact contact, Bitmap bitmap) {
        contactPhotoCache.put(contact.getContactId(), bitmap);
    }

    @Override
    public void invalidate() {
    }
}

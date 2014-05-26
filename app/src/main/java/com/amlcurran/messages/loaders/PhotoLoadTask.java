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
import android.content.res.Resources;
import android.graphics.Bitmap;

import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.data.Contact;

import java.util.concurrent.Callable;

class PhotoLoadTask implements Callable<Object> {

    private final PhotoLoadListener photoLoadListener;
    private final Bitmap defaultImage;
    private final Contact contact;
    private final ContactImageLoader contactImageLoader;

    public PhotoLoadTask(ContentResolver contentResolver, Resources resources, Contact contact, PhotoLoadListener photoLoadListener) {
        this.contact = contact;
        this.contactImageLoader = new ContactImageLoader(contentResolver, resources);
        this.defaultImage = contactImageLoader.getDefaultImage();
        this.photoLoadListener = photoLoadListener;
    }

    @Override
    public Object call() throws Exception {

        Bitmap result = null;
        if (contact.getPhotoId() >= 0) {
            result = contactImageLoader.getLargeImage(contact.getPhotoId());
        }
        if (result == null) {
            result = defaultImage;
        }

        photoLoadListener.onPhotoLoaded(result);
        return null;
    }

}

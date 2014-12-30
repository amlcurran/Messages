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

package com.amlcurran.messages.loaders.photos;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;

import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.MessagesCache;

import java.util.concurrent.Callable;

class PhotoLoadTask implements Callable<Object> {

    private final PhotoLoadListener photoLoadListener;
    private final Bitmap defaultImage;
    private final Contact contact;
    private final MessagesCache cache;
    private final Handler uiHandler;
    private final ContactImageLoader contactImageLoader;

    public PhotoLoadTask(ContentResolver contentResolver, Resources resources, Contact contact, PhotoLoadListener photoLoadListener, MessagesCache cache, Handler uiHandler) {
        this.contact = contact;
        this.cache = cache;
        this.uiHandler = uiHandler;
        this.contactImageLoader = new ContactImageLoader(contentResolver, resources);
        this.defaultImage = contactImageLoader.getDefaultImage();
        this.photoLoadListener = photoLoadListener;
    }

    @Override
    public Object call() throws Exception {

        Bitmap result = null;

        if (cache.getContactPhoto(contact) != null) {
            photoFromCache();
            return null;
        }

        beforePhotoLoad();

        if (contact.getPhotoId() >= 0) {
            result = contactImageLoader.getLargeImage(contact.getPhotoId());
        }
        if (result == null) {
            result = defaultImage;
        }

        cache.storeContactPhoto(contact, result);
        photoLoaded(result);
        return null;
    }

    private void photoLoaded(final Bitmap result) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                photoLoadListener.photoLoaded(result);
            }
        });
    }

    private void beforePhotoLoad() {
        uiHandler.post(new Runnable() {

            @Override
            public void run() {
                photoLoadListener.beforePhotoLoad(contact);
            }

        });
    }

    private void photoFromCache() {
        uiHandler.post(new Runnable() {

            @Override
            public void run() {
                photoLoadListener.photoLoadedFromCache(cache.getContactPhoto(contact));
            }

        });
    }

}

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
import android.content.ContentUris;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;

import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;

import java.util.concurrent.Callable;

class PhotoLoadTask implements Callable<Object> {

    private final ContentResolver contentResolver;
    private final long contactId;
    private final PhotoLoadListener photoLoadListener;
    private final Bitmap defaultImage;

    public PhotoLoadTask(ContentResolver contentResolver, Resources resources, long contactId, PhotoLoadListener photoLoadListener) {
        this.defaultImage = ((BitmapDrawable) resources.getDrawable(R.drawable.ic_contact_picture_unknown)).getBitmap();
        this.contentResolver = contentResolver;
        this.contactId = contactId;
        this.photoLoadListener = photoLoadListener;
    }

    @Override
    public Object call() throws Exception {

        Bitmap result = null;
        if (contactId >= 0) {

            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, contactId);
            Cursor cursor = contentResolver.query(contactUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
            if (cursor.moveToFirst()) {
                try {
                    byte[] blob = cursor.getBlob(0);
                    result = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                } finally {
                    cursor.close();
                }
            }
        }

        if (result == null) {
            result = defaultImage;
        }

        photoLoadListener.onPhotoLoaded(result);
        return null;
    }
}

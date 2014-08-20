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

package com.amlcurran.messages.ui.contact;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.data.Contact;

class AlphaInSettingListener implements PhotoLoadListener {

    private final ImageView contactImageView;

    public AlphaInSettingListener(ImageView contactImageView) {
        this.contactImageView = contactImageView;
    }

    @Override
    public void photoLoaded(Bitmap photo) {
        setPhoto(photo);
    }

    @Override
    public void photoLoadedFromCache(Bitmap photo) {
        setPhoto(photo);
    }

    @Override
    public void beforePhotoLoad(Contact contact) {
        contactImageView.setAlpha(0f);
    }

    private void setPhoto(Bitmap photo) {
        contactImageView.setImageBitmap(photo);
        contactImageView.animate().alpha(1f).start();
    }
}

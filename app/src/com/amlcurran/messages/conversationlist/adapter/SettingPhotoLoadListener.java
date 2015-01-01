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

package com.amlcurran.messages.conversationlist.adapter;

import android.graphics.Bitmap;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.data.Contact;

class SettingPhotoLoadListener implements PhotoLoadListener {

    private final ImageView imageView;
    private float animationLength;

    public SettingPhotoLoadListener(ImageView imageView, float animationLength) {
        this.imageView = imageView;
        this.animationLength = animationLength;
    }

    @Override
    public void photoLoaded(final Bitmap photo) {
        imageView.setImageBitmap(photo);
        ViewPropertyAnimator propertyAnimator = imageView.animate();
        propertyAnimator
                .translationXBy(animationLength)
                .alpha(1f);
        imageView.setTag(propertyAnimator);
    }

    @Override
    public void photoLoadedFromCache(final Bitmap photo) {
        imageView.setImageBitmap(photo);
        imageView.setAlpha(1f);
    }

    @Override
    public void beforePhotoLoad(Contact contact) {
        if (imageView.getTag() != null) {
            ((ViewPropertyAnimator) imageView.getTag()).cancel();
        }
        resetContactImage(imageView);
    }

    private void resetContactImage(ImageView imageView) {
        imageView.setTranslationX(-animationLength);
        imageView.setAlpha(0f);
        imageView.setImageBitmap(null);
    }

}

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

import android.view.ViewGroup;
import android.widget.ImageView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.photos.PhotoLoader;
import com.amlcurran.messages.core.loaders.Task;

class EndToEndPhotoManager implements PhotoLoaderManager {
    private final PhotoLoadListener photoLoadListener;
    private Task currentPhotoTask;

    public EndToEndPhotoManager(ViewGroup host) {
        ImageView contactImageView = (ImageView) host.findViewById(R.id.image);
        this.photoLoadListener = new AlphaInSettingListener(contactImageView);
    }

    @Override
    public void loadContactPhoto(Contact contact, PhotoLoader loader) {
        currentPhotoTask = loader.loadPhoto(contact, photoLoadListener);
    }

    @Override
    public void stopLoadingPhoto() {
        if (currentPhotoTask != null) {
            currentPhotoTask.cancel();
        }
    }
}
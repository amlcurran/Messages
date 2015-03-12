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

import android.content.res.Resources;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.loaders.photos.PhotoLoader;

public class AdapterPhotoLoader {
    private final PhotoLoader loader;
    private final float animationLength;

    public AdapterPhotoLoader(PhotoLoader loader, Resources resources) {
        this.loader = loader;
        this.animationLength = resources.getDimension(R.dimen.photo_animation_length);
    }

    void loadContactPhoto(ConversationViewHolder viewHolder, Conversation item) {
        Contact contact = item.getContact();
        viewHolder.getImageView().setImageBitmap(null);
        viewHolder.setImageTask(loader.loadPhoto(contact, new SettingPhotoLoadListener(viewHolder.getImageView(), animationLength)));
    }

    void stopLoadingPhoto(ConversationViewHolder viewHolder) {
        if (viewHolder.getImageTask() != null) {
            viewHolder.getImageTask().cancel();
        }
    }
}
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

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.loaders.MessagesLoader;

class AdapterPhotoLoader {
    private final MessagesLoader loader;
    private final float animationLength;

    public AdapterPhotoLoader(MessagesLoader loader, float animationLength) {
        this.loader = loader;
        this.animationLength = animationLength;
    }

    void loadContactPhoto(ConversationViewHolder viewHolder, Conversation item) {
        Contact contact = item.getContact();
        viewHolder.imageTask = loader.loadPhoto(contact, new SettingPhotoLoadListener(viewHolder.imageView, animationLength));
    }

    void stopLoadingPhoto(ConversationViewHolder viewHolder) {
        if (viewHolder.imageTask != null) {
            viewHolder.imageTask.cancel();
        }
    }
}
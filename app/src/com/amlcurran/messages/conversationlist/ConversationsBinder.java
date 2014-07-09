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

package com.amlcurran.messages.conversationlist;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.github.amlcurran.sourcebinder.SimpleBinder;

public class ConversationsBinder extends SimpleBinder<Conversation> {

    private static final int IS_UNREAD = 1;
    private static final int IS_READ = 0;
    private final float animationLength;
    private final MessagesLoader loader;

    public ConversationsBinder(Resources resources, MessagesLoader loader) {
        this.loader = loader;
        this.animationLength = resources.getDimension(R.dimen.photo_animation_length);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position, Conversation item) {
        return item.isRead() ? IS_READ : IS_UNREAD;
    }

    @Override
    public View bindView(View convertView, Conversation item, int position) {
        TextView textView1 = getTextView(convertView, android.R.id.text1);
        TextView textView2 = getTextView(convertView, android.R.id.text2);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.image);

        if (isNotSameItem(convertView, item)) {
            loadContactPhoto(item, imageView);
        }

        textView1.setText(item.getContact().getDisplayName());
        textView2.setText(item.getBody());

        convertView.setTag(item);
        return convertView;
    }

    private static boolean isNotSameItem(View convertView, Conversation item) {
        return convertView.getTag() != item;
    }

    private void loadContactPhoto(final Conversation item, final ImageView imageView) {

        final Contact contact = item.getContact();

        loader.loadPhoto(contact, new PhotoLoadListener() {

            @Override
            public void photoLoaded(final Bitmap photo) {
                imageView.setImageBitmap(photo);
                imageView.setTranslationX(-animationLength);
                imageView.animate()
                        .translationXBy(animationLength)
                        .alpha(1f).start();
            }

            @Override
            public void photoLoadedFromCache(final Bitmap photo) {
                imageView.setImageBitmap(photo);
            }

            @Override
            public void beforePhotoLoad(Contact contact) {
                resetContactImage(imageView);
            }

        });

    }

    private void resetContactImage(ImageView imageView) {
        imageView.setImageBitmap(null);
        imageView.setAlpha(0f);
    }

    private TextView getTextView(View convertView, int text1) {
        return (TextView) convertView.findViewById(text1);
    }

    @Override
    public View createView(Context context, int itemViewType, ViewGroup parent) {
        if (itemViewType == IS_READ) {
            return LayoutInflater.from(context).inflate(R.layout.item_conversation_read, parent, false);
        } else {
            return LayoutInflater.from(context).inflate(R.layout.item_conversation_unread, parent, false);
        }
    }
}

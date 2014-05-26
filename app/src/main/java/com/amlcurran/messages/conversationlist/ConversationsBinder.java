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
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.data.Conversation;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.espian.utils.data.SimpleBinder;

public class ConversationsBinder extends SimpleBinder<Conversation> {

    private Resources resources;
    private MessagesLoader loader;

    public ConversationsBinder(Resources resources, MessagesLoader loader) {
        this.resources = resources;
        this.loader = loader;
    }

    @Override
    public View bindView(View convertView, Conversation item, int position) {
        TextView textView1 = getTextView(convertView, android.R.id.text1);
        TextView textView2 = getTextView(convertView, android.R.id.text2);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        resetContactImage(imageView);

        textView1.setText(item.getContact().getDisplayName());
        textView2.setText(item.getBody());
        loader.loadPhoto(item.getContact(), new PhotoLoadListener() {

            @Override
            public void onPhotoLoaded(final Bitmap photo) {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(photo);
                        imageView.animate().alpha(1f).start();
                    }
                });
            }
        });

        if (item.isRead()) {
            setReadStyle(convertView, textView1, textView2);
        } else {
            setUnreadStyle(convertView, textView1, textView2);
        }

        return convertView;
    }

    private void resetContactImage(ImageView imageView) {
        imageView.setImageBitmap(null);
        imageView.setAlpha(0f);
    }

    private void setReadStyle(View convertView, TextView textView1, TextView textView2) {
        textView1.setTypeface(null, 0);
        textView2.setTypeface(null, 0);
        textView1.setTextColor(resources.getColor(android.R.color.primary_text_light));
        textView2.setTextColor(resources.getColor(android.R.color.tertiary_text_light));
    }

    private void setUnreadStyle(View convertView, TextView textView1, TextView textView2) {
        textView1.setTypeface(null, Typeface.BOLD);
        textView2.setTypeface(null, Typeface.BOLD);
        textView1.setTextColor(resources.getColor(R.color.theme_colour));
        textView2.setTextColor(resources.getColor(android.R.color.primary_text_light));
    }

    private TextView getTextView(View convertView, int text1) {
        return (TextView) convertView.findViewById(text1);
    }

    @Override
    public View createView(Context context, int itemViewType) {
        return LayoutInflater.from(context).inflate(R.layout.item_message_preview, null, false);
    }
}

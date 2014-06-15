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

package com.amlcurran.messages.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.MessagesLoader;

public class ContactView extends LinearLayout {

    private final ImageView contactImageView;
    private final TextView nameTextField;
    private final TextView secondTextField;

    public ContactView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.view_contact, this, true);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ContactView, defStyle, R.style.ContactView);
        int titleAppearance = array.getResourceId(R.styleable.ContactView_titleTextAppearance, R.style.ConversationTitle);
        int secondaryAppearance = array.getResourceId(R.styleable.ContactView_secondaryTextAppearance, R.style.ConversationText);
        array.recycle();

        contactImageView = (ImageView) findViewById(R.id.image);
        nameTextField = (TextView) findViewById(android.R.id.text1);
        secondTextField = (TextView) findViewById(android.R.id.text2);

        nameTextField.setTextAppearance(context, titleAppearance);
        secondTextField.setTextAppearance(context, secondaryAppearance);
    }

    public void setContact(final Contact contact, MessagesLoader loader) {
        post(new Runnable() {
            @Override
            public void run() {
                nameTextField.setText(contact.getDisplayName());
                secondTextField.setText(contact.getNumber());
                contactImageView.setImageDrawable(null);
                contactImageView.setAlpha(0f);
            }
        });
        loader.loadPhoto(contact, new PhotoLoadListener() {
            @Override
            public void onPhotoLoaded(final Bitmap photo) {
                contactImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        contactImageView.setImageBitmap(photo);
                        contactImageView.animate().alpha(1f).start();
                    }
                });
            }
        });
    }

}

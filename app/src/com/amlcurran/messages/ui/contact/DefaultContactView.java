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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.photos.PhotoLoader;

public class DefaultContactView extends LinearLayout implements ContactView {

    private final ViewContactClickListener viewContactClickListener = new ViewContactClickListener();
    private final TwoViewContactFormatter contactFormatter;
    private final EndToEndPhotoManager photoLoaderManager;

    public DefaultContactView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultContactView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(LayoutInflater.from(context));
        photoLoaderManager = new EndToEndPhotoManager(this);
        contactFormatter = new TwoViewContactFormatter(this);
    }

    protected void inflate(LayoutInflater inflater) {
        inflater.inflate(R.layout.view_contact, this, true);
    }

    @Override
    public void setContact(final Contact contact, PhotoLoader loader) {
        photoLoaderManager.stopLoadingPhoto();
        viewContactClickListener.contact = contact;
        contactFormatter.format(contact);
        photoLoaderManager.loadContactPhoto(contact, loader);
    }

    @Override
    public void setClickToView(ContactClickListener callback) {
        viewContactClickListener.callback = callback;
        setOnClickListener(viewContactClickListener);
    }

}

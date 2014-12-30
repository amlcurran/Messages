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

public class RoundContactView extends LinearLayout implements ContactView {

    private final ContactFormatter contactFormatter;
    private final PhotoLoaderManager photoLoaderManager;
    private final ViewContactClickListener viewContactClickListener = new ViewContactClickListener();

    public RoundContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(LayoutInflater.from(context));
        photoLoaderManager = new EndToEndPhotoManager(this);
        contactFormatter = new TwoViewContactFormatter(this);
    }

    protected void inflate(LayoutInflater inflater) {
        inflater.inflate(R.layout.view_contact_round, this, true);
    }

    @Override
    public void setContact(final Contact contact, PhotoLoader loader) {
        photoLoaderManager.stopLoadingPhoto();
        contactFormatter.format(contact);
        viewContactClickListener.contact = contact;
        photoLoaderManager.loadContactPhoto(contact, loader);
    }

    @Override
    public void setClickToView(ContactClickListener callback) {
        viewContactClickListener.callback = callback;
        setOnClickListener(viewContactClickListener);
    }

}

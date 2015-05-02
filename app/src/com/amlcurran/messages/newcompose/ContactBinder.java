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

package com.amlcurran.messages.newcompose;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.photos.PhotoLoader;
import com.amlcurran.messages.ui.contact.ContactView;
import com.amlcurran.messages.ui.contact.RoundContactView;
import com.amlcurran.sourcebinder.SimpleBinder;

class ContactBinder extends SimpleBinder<Contact> {

    private final PhotoLoader photoLoader;

    public ContactBinder(PhotoLoader photoLoader) {
        this.photoLoader = photoLoader;
    }

    @Override
    public View bindView(View convertView, Contact item, int position) {
        ((ContactView) convertView).setContact(item, photoLoader);
        return convertView;
    }

    @Override
    public View createView(Context context, int itemViewType, ViewGroup parent) {
        return new RoundContactView(context, null);
    }

}

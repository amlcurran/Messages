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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.Task;

public class RoundContactView extends LinearLayout implements ContactView {

    private final ImageView contactImageView;
    private final ContactFormatter contactFormatter;
    private Task currentPhotoTask;

    public RoundContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(LayoutInflater.from(context));
        TextView nameTextField = ((TextView) findViewById(android.R.id.text1));
        TextView secondTextField = ((TextView) findViewById(android.R.id.text2));
        contactImageView = ((ImageView) findViewById(R.id.image));
        contactFormatter = new TwoViewContactFormatter(nameTextField, secondTextField);
    }

    protected void inflate(LayoutInflater inflater) {
        inflater.inflate(R.layout.view_contact_round, this, true);
    }

    @Override
    public void setContact(final Contact contact, MessagesLoader loader) {
        cancelCurrentTask();
        contactFormatter.format(contact);
        currentPhotoTask = loader.loadPhoto(contact, new AlphaInSettingListener(contactImageView));
    }

    private void cancelCurrentTask() {
        if (currentPhotoTask != null) {
            currentPhotoTask.cancel();
        }
    }

}

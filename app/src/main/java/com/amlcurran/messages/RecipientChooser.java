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

package com.amlcurran.messages;

import android.view.View;
import android.widget.AdapterView;

import com.amlcurran.messages.core.data.Contact;

import java.util.List;

public class RecipientChooser implements AdapterView.OnItemClickListener {

    private final ChooserListener chooserListener;
    private List<Contact> contacts;

    public RecipientChooser(ChooserListener chooserListener) {
        this.chooserListener = chooserListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        chooserListener.recipientChosen(contacts.get(position));
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public static interface ChooserListener {
        void recipientChosen(Contact contact);
    }
}

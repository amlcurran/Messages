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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.data.ParcelablePhoneNumber;
import com.amlcurran.messages.ui.contact.ContactChipView;

public class PersonPickerView extends LinearLayout {

    private final EditText numberEntry;
    private final ContactChipView personChip;
    private PersonPickerView.Listener listener;
    private String enteredAddress;

    public PersonPickerView(Context context) {
        this(context, null, 0);
    }

    public PersonPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PersonPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.view_person_picker, this);
        personChip = (ContactChipView) findViewById(R.id.new_compose_chip);
        personChip.setRemoveListener(new BubbleUpRemoveListener());
        numberEntry = ((EditText) findViewById(R.id.new_pick_person));
    }

    public void chosenRecipient(Contact contact) {
        personChip.setVisibility(View.VISIBLE);
        personChip.setContact(contact, SingletonManager.getMessagesLoader(getContext()));
        numberEntry.setText(contact.getNumber().flatten());
    }

    public PhoneNumber getEnteredAddress() {
        return new ParcelablePhoneNumber(numberEntry.getText().toString());
    }

    public void setEnteredAddress(String enteredAddress) {
        numberEntry.setText(enteredAddress);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void removedRecipient(Contact contact);
    }

    private class BubbleUpRemoveListener implements ContactChipView.RemoveListener {
        @Override
        public void chipRemoveRequested(ContactChipView contactChipView, Contact contact) {
            if (listener != null) {
                listener.removedRecipient(contact);
            }
            contactChipView.setVisibility(View.GONE);
            numberEntry.setText("");
        }
    }
}

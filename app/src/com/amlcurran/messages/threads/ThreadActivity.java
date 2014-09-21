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

package com.amlcurran.messages.threads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.amlcurran.messages.ActivityController;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.ParcelablePhoneNumber;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.ui.contact.ContactClickListener;

public class ThreadActivity extends Activity implements SmsComposeListener, MessagesLoaderProvider, ContactClickListener {

    public static final String THREAD_ID = "threadId";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String CONTACT_BUNDLE = "contactBundle";
    public static final String COMPOSED_MESSAGE = "composedMessage";
    private ActivityController activityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        activityController = new ActivityController(this, null);

        String threadId = getIntent().getStringExtra(THREAD_ID);
        PhoneNumber address = new ParcelablePhoneNumber(getIntent().getStringExtra(PHONE_NUMBER));
        Bundle contactBundle = getIntent().getBundleExtra(CONTACT_BUNDLE);
        String composedMesage = getIntent().getStringExtra(COMPOSED_MESSAGE);

        ThreadFragment fragment = ThreadFragment.create(threadId, address, contactBundle, composedMesage);
        getFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }

    public static Intent intent(Activity activity, String threadId, Contact contact, String writtenMessage) {
        Intent intent = new Intent(activity, ThreadActivity.class);
        intent.putExtra(THREAD_ID, threadId);
        intent.putExtra(PHONE_NUMBER, contact.getNumber().flatten());
        intent.putExtra(CONTACT_BUNDLE, ContactFactory.smooshContact(contact));
        intent.putExtra(COMPOSED_MESSAGE, writtenMessage);
        return intent;
    }

    @Override
    public void sendSms(InFlightSmsMessage smsMessage) {
        activityController.sendSms(smsMessage);
    }

    @Override
    public void callNumber(PhoneNumber phoneNumber) {
        activityController.callNumber(phoneNumber);
    }

    @Override
    public void displayThread(Contact contact, int threadId, String writtenMessage) {

    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return SingletonManager.getMessagesLoader(this);
    }

    @Override
    public void viewContact(Contact contact) {
        activityController.viewContact(ContactFactory.uriForContact(contact, getContentResolver()));
    }

    @Override
    public void addContact(Contact contact) {
        activityController.addContact(contact.getNumber());
    }
}

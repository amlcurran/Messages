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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;

import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.notifications.BlockingInUiNotifier;
import com.amlcurran.messages.notifications.Dialog;
import com.amlcurran.messages.threads.ThreadActivity;

public class ActivityController {

    private static final int SAVE_CONTACT = 20202;
    private final Activity activity;
    private BlockingInUiNotifier blockingInUiNotifier;

    public ActivityController(Activity activity, BlockingInUiNotifier blockingInUiNotifier) {
        this.activity = activity;
        this.blockingInUiNotifier = blockingInUiNotifier;
    }

    public void callNumber(PhoneNumber phoneNumber) {
        Uri telUri = Uri.parse("tel:" + phoneNumber.flatten());
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(telUri);
        activity.startActivity(intent);
    }

    public void switchSmsApp() {
        String title = activity.getString(R.string.mms_confirm_title);
        String message = activity.getString(R.string.mms_confirm_message);

        Dialog.Button cancelButton = new Dialog.Button(activity.getString(android.R.string.cancel));
        Dialog.Button continueButton = new Dialog.Button(activity.getString(R.string.continue_text));

        BlockingInUiNotifier.Callbacks callbacks = new BlockingInUiNotifier.Callbacks() {

            @Override
            public void positive() {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.getPackageName());
                activity.startActivity(intent);
            }

            @Override
            public void negative() {

            }

        };

        blockingInUiNotifier.show(callbacks, title, message, cancelButton, continueButton);
    }

    public void viewContact(Uri contactUri) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setData(contactUri);
        activity.startActivity(viewIntent);
    }

    public void addContact(PhoneNumber number) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, PhoneNumberUtils.stripSeparators(number.flatten()));
        activity.startActivityForResult(intent, SAVE_CONTACT);
    }

    public void showAbout() {
        activity.startActivity(SecondaryActivity.about(activity));
    }

    public void showPreferences() {
        activity.startActivity(SecondaryActivity.preferences(activity));
    }

    public void showThreadActivity(String threadId, Bundle contactBundle, String writtenMessage) {
        activity.startActivity(ThreadActivity.intent(activity, threadId, contactBundle, writtenMessage));
    }
}

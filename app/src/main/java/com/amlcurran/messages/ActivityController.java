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
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;

import com.amlcurran.messages.data.SmsMessage;
import com.amlcurran.messages.telephony.SmsSender;

public class ActivityController {

    private final Activity activity;

    public ActivityController(Activity activity) {
        this.activity = activity;
    }

    void callNumber(String sendAddress) {
        Uri telUri = Uri.parse("tel:" + PhoneNumberUtils.stripSeparators(sendAddress));
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(telUri);
        activity.startActivity(intent);
    }

    public void switchSmsApp() {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.getPackageName());
        activity.startActivityForResult(intent, MessagesActivity.REQUEST_CHANGE_SMS_APP);
    }

    public void viewContact(Uri contactUri) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setData(contactUri);
        activity.startActivity(viewIntent);
    }

    public void showAbout() {
        activity.startActivity(new Intent(activity, AboutActivity.class));
    }

    public void sendSms(SmsMessage message) {
        Intent intent = new Intent(activity, SmsSender.class);
        intent.setAction(SmsSender.ACTION_SEND_REQUEST);
        intent.putExtra(SmsSender.EXTRA_MESSAGE, message);
        activity.startService(intent);
    }
}

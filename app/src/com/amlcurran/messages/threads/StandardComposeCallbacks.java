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

import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.telephony.SynchronousDatabaseWriter;
import com.amlcurran.messages.ui.ComposeMessageView;

import java.util.Calendar;

class StandardComposeCallbacks implements ComposeMessageView.ComposureCallbacks {
    private final Activity activity;
    private final PhoneNumber phoneNumber;
    private final SmsComposeListener listener;

    public StandardComposeCallbacks(Activity activity, PhoneNumber phoneNumber, SmsComposeListener listener) {
        this.activity = activity;
        this.phoneNumber = phoneNumber;
        this.listener = listener;
    }

    @Override
    public void onMessageComposed(CharSequence body) {
        String message = String.valueOf(body);
        long timestamp = Calendar.getInstance().getTimeInMillis();
        InFlightSmsMessage smsMessage = new InFlightSmsMessage(phoneNumber, message, Time.fromMillis(timestamp));
        listener.sendSms(smsMessage);
        new SynchronousDatabaseWriter(activity).clearDraft(phoneNumber);
    }
}

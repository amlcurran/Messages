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

package com.amlcurran.messages.telephony;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import com.amlcurran.messages.data.InFlightSmsMessage;

import java.util.ArrayList;

public class SmsSenderReal {
    private final SmsSender smsSender;
    private final SmsManager smsManager;

    public SmsSenderReal(SmsSender smsSender, SmsManager smsManager) {
        this.smsSender = smsSender;
        this.smsManager = smsManager;
    }

    void send(InFlightSmsMessage message, Uri inserted) {
        ArrayList<PendingIntent> messageSendIntents = getMessageSendIntents(message, inserted);
        smsManager.sendMultipartTextMessage(message.getPhoneNumber().flatten(), null, smsManager.divideMessage(message.getBody()), messageSendIntents, null);
    }

    ArrayList<PendingIntent> getMessageSendIntents(InFlightSmsMessage message, Uri inserted) {
        Intent intent = new Intent(smsSender, SmsReceiver.class);
        intent.putExtra(SmsSender.EXTRA_MESSAGE, message);
        intent.putExtra(SmsSender.EXTRA_OUTBOX_URI, inserted.toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(smsSender, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>();
        pendingIntents.add(pendingIntent);
        return pendingIntents;
    }
}
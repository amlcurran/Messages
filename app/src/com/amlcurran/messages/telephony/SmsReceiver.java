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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.InFlightSmsMessageFactory;

public class SmsReceiver extends BroadcastReceiver {

    static final String ASYNC_WRITE = "com.amlcurran.messages.smsreceiver.ASYNC_WRITE";
    static final String EXTRA_MESSAGE = "message";
    static final String EXTRA_RESULT = "result";
    static final String EXTRA_WRITE_TYPE = "write_type";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_DELIVER_ACTION.equals(intent.getAction())) {

            android.telephony.SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            InFlightSmsMessage inFlightSmsMessage = InFlightSmsMessageFactory.fromDeliverBroadcast(messages);
            Intent asyncWriteIntent = SmsAsyncService.getAsyncWriteIntent(context, inFlightSmsMessage, WriteType.INBOX);
            context.startService(asyncWriteIntent);

        } else {

            int resultCode = getResultCode();

            Intent sentIntent = new Intent(context, SmsSender.class);
            sentIntent.setAction(SmsSender.ACTION_MESSAGE_SENT);
            sentIntent.putExtras(intent.getExtras());
            sentIntent.putExtra(EXTRA_RESULT, resultCode);

            context.startService(sentIntent);

        }
    }

}

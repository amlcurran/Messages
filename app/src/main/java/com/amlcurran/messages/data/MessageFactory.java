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

package com.amlcurran.messages.data;

import android.database.Cursor;
import android.provider.Telephony;

import com.amlcurran.messages.core.data.Message;
import com.espian.utils.data.CursorHelper;

public class MessageFactory {

    public static SmsMessage fromDeliverBroadcast(android.telephony.SmsMessage[] messages) {
        if (messages.length == 0) {
            throw new NullPointerException("Creating SMS message from empty array");
        }
        String address = messages[0].getDisplayOriginatingAddress();
        String body = createBody(messages);
        long timestamp = messages[0].getTimestampMillis();
        return new SmsMessage(address, body, timestamp, false, false);
    }

    public static Message fromCursor(Cursor cursor) {
        String body = CursorHelper.asString(cursor, Telephony.Sms.BODY);
        long timestamp = CursorHelper.asLong(cursor, Telephony.Sms.DATE);
        boolean isSending = CursorHelper.asInt(cursor, Telephony.Sms.TYPE) == Telephony.Sms.MESSAGE_TYPE_OUTBOX;
        boolean isFromMe = CursorHelper.asInt(cursor, Telephony.Sms.TYPE) == Telephony.Sms.MESSAGE_TYPE_SENT || isSending;
        String address = CursorHelper.asString(cursor, Telephony.Sms.ADDRESS);
        SmsMessage smsMessage = new SmsMessage(address, body, timestamp, isFromMe, isSending);
        smsMessage.id = CursorHelper.asLong(cursor, Telephony.Sms._ID);
        return smsMessage;
    }

    private static String createBody(android.telephony.SmsMessage[] messages) {
        String result = "";
        for (android.telephony.SmsMessage message : messages) {
            result += message.getDisplayMessageBody();
        }
        return result;
    }
}

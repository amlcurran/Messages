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

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.provider.Telephony;

import com.amlcurran.messages.core.data.Message;
import com.espian.utils.data.CursorHelper;

public class MessageFactory {

    private static final int IS_FROM_OTHER = 1;
    private static final int IS_FROM_ME = 0;
    private static final int IS_NOT_SENDING = 2;
    private static final int IS_SENDING = 4;

    public static InFlightSmsMessage fromDeliverBroadcast(android.telephony.SmsMessage[] messages) {
        if (messages.length == 0) {
            throw new NullPointerException("Creating SMS message from empty array");
        }
        String address = messages[0].getDisplayOriginatingAddress();
        String body = createBody(messages);
        long timestamp = messages[0].getTimestampMillis();
        return new InFlightSmsMessage(address, body, timestamp);
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

    static SmsMessage fromParcel(Parcel in) {
        return new SmsMessage(in.readString(), in.readString(), in.readLong(), in.readInt() == IS_FROM_ME, in.readInt() == IS_SENDING);
    }

    static void toParcel(SmsMessage smsMessage, Parcel dest) {
        dest.writeString(smsMessage.getAddress());
        dest.writeString(smsMessage.getBody());
        dest.writeLong(smsMessage.getTimestamp());
        dest.writeInt(smsMessage.isFromMe() ? IS_FROM_ME : IS_FROM_OTHER);
        dest.writeInt(smsMessage.isSending() ? IS_SENDING : IS_NOT_SENDING);
    }

    public static ContentValues toContentValues(InFlightSmsMessage smsMessage, int messageTypeSent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Telephony.Sms.Inbox.BODY, smsMessage.getBody());
        contentValues.put(Telephony.Sms.Inbox.ADDRESS, smsMessage.getAddress());
        contentValues.put(Telephony.Sms.Inbox.DATE, smsMessage.getTimestamp());
        contentValues.put(Telephony.Sms.Inbox.DATE_SENT, smsMessage.getTimestamp());
        contentValues.put(Telephony.Sms.Inbox.TYPE, messageTypeSent);
        return contentValues;
    }
}

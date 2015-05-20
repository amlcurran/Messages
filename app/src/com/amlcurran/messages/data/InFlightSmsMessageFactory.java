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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.sourcebinder.CursorHelper;

public class InFlightSmsMessageFactory {

    public static InFlightSmsMessage fromDeliverBroadcast(android.telephony.SmsMessage[] messages) {
        if (messages.length == 0) {
            throw new NullPointerException("Creating SMS message from empty array");
        }
        ParcelablePhoneNumber phoneNumber = new ParcelablePhoneNumber(messages[0].getDisplayOriginatingAddress());
        String body = createBody(messages);
        long timestamp = messages[0].getTimestampMillis();
        return new InFlightSmsMessage(phoneNumber, body, Time.fromMillis(timestamp));
    }

    public static SmsMessage fromCursor(Cursor cursor) {
        String body = CursorHelper.asString(cursor, Telephony.Sms.BODY);
        long timestamp = CursorHelper.asLong(cursor, Telephony.Sms.DATE);
        int messageType = CursorHelper.asInt(cursor, Telephony.Sms.TYPE);
        String address = CursorHelper.asString(cursor, Telephony.Sms.ADDRESS);
        String threadId = CursorHelper.asString(cursor, Telephony.Sms.THREAD_ID);
        long id = CursorHelper.asLong(cursor, Telephony.Sms._ID);
        return new SmsMessage(id, threadId, new ParcelablePhoneNumber(address), body, Time.fromMillis(timestamp), fromApi(messageType));
    }

    private static String createBody(android.telephony.SmsMessage[] messages) {
        String result = "";
        for (android.telephony.SmsMessage message : messages) {
            result += message.getDisplayMessageBody();
        }
        return result;
    }

    public static ContentValues toContentValues(InFlightSmsMessage smsMessage, int messageTypeSent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Telephony.Sms.Inbox.BODY, smsMessage.getBody());
        contentValues.put(Telephony.Sms.Inbox.ADDRESS, smsMessage.getPhoneNumber().flatten());
        contentValues.put(Telephony.Sms.Inbox.DATE, smsMessage.getTimestamp().toMillis());
        contentValues.put(Telephony.Sms.Inbox.DATE_SENT, smsMessage.getTimestamp().toMillis());
        contentValues.put(Telephony.Sms.Inbox.TYPE, messageTypeSent);
        contentValues.put(Telephony.Sms.Inbox.READ, messageTypeSent == Telephony.Sms.Inbox.MESSAGE_TYPE_INBOX ? 0 : 1);
        return contentValues;
    }

    private static SmsMessage.Type fromApi(int apiType) {
        switch (apiType) {

            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                return SmsMessage.Type.INBOX;

            case Telephony.Sms.MESSAGE_TYPE_DRAFT:
                return SmsMessage.Type.DRAFT;

            case Telephony.Sms.MESSAGE_TYPE_FAILED:
                return SmsMessage.Type.FAILED;

            case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                return SmsMessage.Type.SENDING;

            case Telephony.Sms.MESSAGE_TYPE_SENT:
                return SmsMessage.Type.SENT;

        }
        return SmsMessage.Type.UNKNOWN;
    }

    public static int toApi(SmsMessage.Type type) {
        switch (type) {

            case INBOX:
                return Telephony.Sms.MESSAGE_TYPE_INBOX;

            case DRAFT:
                return Telephony.Sms.MESSAGE_TYPE_DRAFT;

            case FAILED:
                return Telephony.Sms.MESSAGE_TYPE_FAILED;

            case SENDING:
                return Telephony.Sms.MESSAGE_TYPE_OUTBOX;

            case SENT:
                return Telephony.Sms.MESSAGE_TYPE_SENT;

        }
        return 0;
    }

    public static SmsMessage fromUri(Uri messageUri, ContentResolver contentResolver) {
        Cursor result = contentResolver.query(messageUri, null, null, null, null);
        SmsMessage message = null;
        if (result.moveToFirst()) {
            message = fromCursor(result);
        }
        result.close();
        return message;
    }
}

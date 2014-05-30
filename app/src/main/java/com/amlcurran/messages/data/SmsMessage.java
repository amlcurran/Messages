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
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;

import com.amlcurran.messages.core.data.Message;

public class SmsMessage implements Parcelable, Message {

    private static final int IS_FROM_OTHER = 1;
    private static final int IS_FROM_ME = 0;
    private static final int IS_NOT_SENDING = 2;
    private static final int IS_SENDING = 4;
    private final String address;
    private final String body;
    private final long timestamp;
    private final boolean isFromMe;
    private final boolean isSending;
    long id;

    public SmsMessage(String address, String body, long timestamp, boolean isFromMe, boolean isSending) {
        this.address = address;
        this.body = body;
        this.timestamp = timestamp;
        this.isFromMe = isFromMe;
        this.isSending = isSending;
    }

    private SmsMessage(Parcel in) {
        this.address = in.readString();
        this.body = in.readString();
        this.timestamp = in.readLong();
        this.isFromMe = in.readInt() == IS_FROM_ME;
        this.isSending = in.readInt() == IS_SENDING;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean isFromMe() {
        return isFromMe;
    }

    @Override
    public String toString() {
        return String.format("SMS with address: %1$s\ntimestamp: %2$d\nbody: %3$s", address, timestamp, body);
    }

    public static final Parcelable.Creator<SmsMessage> CREATOR = new Parcelable.Creator<SmsMessage>() {

        public SmsMessage createFromParcel(Parcel in) {
            return new SmsMessage(in);
        }

        public SmsMessage[] newArray(int size) {
            return new SmsMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(body);
        dest.writeLong(timestamp);
        dest.writeInt(isFromMe ? IS_FROM_ME : IS_FROM_OTHER);
        dest.writeInt(isSending ? IS_SENDING : IS_NOT_SENDING);
    }

    public ContentValues toContentValues(int messageTypeSent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Telephony.Sms.Inbox.BODY, body);
        contentValues.put(Telephony.Sms.Inbox.ADDRESS, address);
        contentValues.put(Telephony.Sms.Inbox.DATE, timestamp);
        contentValues.put(Telephony.Sms.Inbox.DATE_SENT, timestamp);
        contentValues.put(Telephony.Sms.Inbox.TYPE, messageTypeSent);
        return contentValues;
    }

    @Override
    public boolean isSending() {
        return isSending;
    }

    @Override
    public long getId() {
        return id;
    }
}

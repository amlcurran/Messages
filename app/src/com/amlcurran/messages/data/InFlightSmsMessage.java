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

import android.os.Parcel;
import android.os.Parcelable;

import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.Time;

public class InFlightSmsMessage implements Parcelable {

    private final ParcelablePhoneNumber phoneNumber;
    private final String body;
    private final Time timestamp;

    public InFlightSmsMessage(PhoneNumber phoneNumber, String message, Time timestamp) {
        this.phoneNumber = new ParcelablePhoneNumber(phoneNumber.flatten());
        this.body = message;
        this.timestamp = timestamp;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public String getBody() {
        return body;
    }

    public Time getTimestamp() {
        return timestamp;
    }

    public static final Creator<InFlightSmsMessage> CREATOR = new Creator<InFlightSmsMessage>() {

        public InFlightSmsMessage createFromParcel(Parcel in) {
            ParcelablePhoneNumber phoneNumber1 = in.readParcelable(getClass().getClassLoader());
            String message = in.readString();
            Time time = Time.fromMillis(in.readLong());
            return new InFlightSmsMessage(phoneNumber1, message, time);
        }

        public InFlightSmsMessage[] newArray(int size) {
            return new InFlightSmsMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(phoneNumber, 0);
        dest.writeString(body);
        dest.writeLong(timestamp.toMillis());
    }
}

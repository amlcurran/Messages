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

import com.amlcurran.messages.core.data.CoreMessage;

public class InFlightSmsMessage implements CoreMessage, Parcelable {

    private String address;
    private String body;
    private long timestamp;

    public InFlightSmsMessage(String address, String body, long timestamp) {
        this.address = address;
        this.body = body;
        this.timestamp = timestamp;
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

    public static final Creator<InFlightSmsMessage> CREATOR = new Creator<InFlightSmsMessage>() {

        public InFlightSmsMessage createFromParcel(Parcel in) {
            return new InFlightSmsMessage(in.readString(), in.readString(), in.readLong());
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
        dest.writeString(address);
        dest.writeString(body);
        dest.writeLong(timestamp);
    }
}

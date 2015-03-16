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

package com.amlcurran.messages.core.threads;

import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.data.Time;

//TODO: is this class a duplication of {@link SmsMessage}?
public class InFlightSmsMessage {

    private final CharSequence messageBody;
    private final PhoneNumber number;
    private final SmsMessage.Type type;
    private final Time time;

    public InFlightSmsMessage(CharSequence messageBody, PhoneNumber number, SmsMessage.Type type, Time time) {
        this.messageBody = messageBody;
        this.number = number;
        this.type = type;
        this.time = time;
    }

    public static InFlightSmsMessage timestampedNow(CharSequence messageBody, PhoneNumber number, SmsMessage.Type type) {
        return new InFlightSmsMessage(messageBody, number, type, Time.now());
    }

    public SmsMessage.Type getType() {
        return type;
    }

    public Time getTimestamp() {
        return time;
    }

    public CharSequence getBody() {
        return messageBody;
    }

    public PhoneNumber getNumber() {
        return number;
    }
}

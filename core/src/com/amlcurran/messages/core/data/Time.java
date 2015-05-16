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

package com.amlcurran.messages.core.data;

import org.joda.time.DateTime;

import java.io.Serializable;

public class Time implements Serializable {

    private final long millis;

    private Time(long millis) {
        this.millis = millis;
    }

    public long toMillis() {
        return millis;
    }

    public static Time fromMillis(long millis) {
        return new Time(millis);
    }

    public static Time fromDateTime(DateTime dateTime) {
        return new Time(dateTime.getMillis());
    }

    public boolean isLaterThan(Time latest) {
        return millis > latest.millis;
    }

    public static Time now() {
        return fromMillis(System.currentTimeMillis());
    }
}

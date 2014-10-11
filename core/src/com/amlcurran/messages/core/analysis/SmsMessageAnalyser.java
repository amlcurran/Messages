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

package com.amlcurran.messages.core.analysis;

import com.amlcurran.messages.core.data.Time;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsMessageAnalyser {

    private static final int ONE_MINUTE_IN_SECS = 60;
    private static final int ONE_HOUR_IN_MINS = 60;
    private final DifferenceStringProvider differencesStringProvider;
    private final DateFormat fullFormatter = new SimpleDateFormat("HH:mm dd-MMM-yy");
    private final DateFormat timeFormatter = new SimpleDateFormat("HH:mm");
    private final Date date = new Date();
    private final DateTime startOfToday;

    public SmsMessageAnalyser(DifferenceStringProvider differencesStringProvider) {
        this.differencesStringProvider = differencesStringProvider;
        this.startOfToday = new DateTime().withTimeAtStartOfDay();
    }

    boolean isYesterday(Time time) {
        DateTime then = new DateTime(time.toMillis());
        return then.withTimeAtStartOfDay().plusDays(1).equals(startOfToday);
    }

    public String getDifferenceToNow(Time time) {
        long millisDifference = Math.abs(time.toMillis() - Time.fromMillis(System.currentTimeMillis()).toMillis());
        long secondsDifference = millisDifference / 1000;
        if (secondsDifference > ONE_MINUTE_IN_SECS) {
            long minutesDifference = secondsDifference / ONE_MINUTE_IN_SECS;
            if (minutesDifference > ONE_HOUR_IN_MINS) {
                long hoursDifference = minutesDifference / ONE_HOUR_IN_MINS;
                if (isToday(time)) {
                    return differencesStringProvider.hoursDifference(hoursDifference);
                } else if (isYesterday(time)) {
                    return differencesStringProvider.yesterday(timeFormatter.format(time.toMillis()));
                } else {
                    date.setTime(time.toMillis());
                    return fullFormatter.format(date);
                }
            } else {
                return differencesStringProvider.minutesDifference(minutesDifference);
            }
        } else {
            return differencesStringProvider.underAMinute();
        }
    }

    boolean isToday(Time time) {
        DateTime then = new DateTime(time.toMillis());
        return then.withTimeAtStartOfDay().equals(startOfToday);
    }
}

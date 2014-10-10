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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SmsMessageAnalyserTest {

    private static final int MILLIS = 1000;
    private static final int MINUTE = MILLIS * 60;
    private static final int HOUR = MINUTE * 60;
    private SmsMessageAnalyser smsMessageAnalyser;
    private boolean underAMinute_called;
    private long minutesDifference_param;
    private long hoursDifference_param;

    @Before
    public void setUp() throws Exception {
        smsMessageAnalyser = new SmsMessageAnalyser(new FakeDifferenceStringProvider());
    }

    @Test
    public void testUnderAMinuteUsesUnderAMinuteAgo() {
        Time time = nowMinusMillis(MILLIS * 3);

        smsMessageAnalyser.getDifferenceToNow(time);

        assertTrue("Didn't use the underAMinute() callback", underAMinute_called);
    }

    @Test
    public void test5MinutesAgo_UsesTheRightCallback() {
        int minutes = 5;
        Time time = nowMinusMillis(MINUTE * minutes);

        smsMessageAnalyser.getDifferenceToNow(time);

        assertEquals("Didn't use the minutes callback", minutes, minutesDifference_param);
    }

    @Test
    public void test2HoursAgo_UsesTheRightCallback() {
        int hours = 2;
        Time time = nowMinusMillis(HOUR * hours);

        smsMessageAnalyser.getDifferenceToNow(time);

        assertEquals("Didn't use the minutes callback", hours, hoursDifference_param);
    }

    @Test
    public void testThisTimeADayAgo_IsYesterday() {
        DateTime twentyFourHoursAgo = new DateTime().minusDays(1);

        assertTrue("24 hours ago should be yesterday", smsMessageAnalyser.isYesterday(Time.fromDateTime(twentyFourHoursAgo)));
    }

    @Test
    public void test23h59mOneDayAgo_IsYesterday() {
        DateTime yesterday2359 = new DateTime().withTimeAtStartOfDay().minusMinutes(1);

        assertTrue("23:59 a day ago should be yesterday", smsMessageAnalyser.isYesterday(Time.fromDateTime(yesterday2359)));
    }

    @Test
    public void test0h01mOneDayAgo_IsYesterday() {
        DateTime yesterday0001 = new DateTime().minusDays(1).withTimeAtStartOfDay().plusMinutes(1);

        assertTrue("00:01 a day ago should be yesterday", smsMessageAnalyser.isYesterday(Time.fromDateTime(yesterday0001)));
    }

    @Test
    public void test00h01mToday_IsToday() {
        DateTime today0001 = new DateTime().withTimeAtStartOfDay().plusMinutes(1);

        assertTrue("00:01 today should be today", smsMessageAnalyser.isToday(Time.fromDateTime(today0001)));
    }

    private Time nowMinusMillis(int millis) {
        long systemTime = System.currentTimeMillis();
        return Time.fromMillis(systemTime - millis);
    }

    private class FakeDifferenceStringProvider implements DifferenceStringProvider {
        @Override
        public String hoursDifference(long hoursDifference) {
            hoursDifference_param = hoursDifference;
            return null;
        }

        @Override
        public String minutesDifference(long minutesDifference) {
            minutesDifference_param = minutesDifference;
            return null;
        }

        @Override
        public String underAMinute() {
            underAMinute_called = true;
            return null;
        }

        @Override
        public String yesterday(String time) {
            return null;
        }
    }
}
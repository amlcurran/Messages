package com.amlcurran.messages.telephony;

import android.telephony.SmsMessage;

public class SmsCounter {

    public SmsCount getSmsDeets(CharSequence text) {
        int[] results = SmsMessage.calculateLength(text, false);
        int length = results[1] + results[2];
        return new SmsCount(results[1], length, results[0]);
    }

    public static class SmsCount {

        public final int usedCharacters;
        public final int messageLength;
        public final int numberOfRequiredSms;

        public SmsCount(int usedCharacters, int messageLength, int numberOfRequiredSms) {
            this.usedCharacters = usedCharacters;
            this.messageLength = messageLength;
            this.numberOfRequiredSms = numberOfRequiredSms;
        }

        @Override
        public String toString() {
            return String.format("Used %1$d characters of a total %2$d in %3$d messages", usedCharacters, messageLength, numberOfRequiredSms);
        }
    }
}

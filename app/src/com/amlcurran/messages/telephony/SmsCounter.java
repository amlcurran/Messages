package com.amlcurran.messages.telephony;

import android.telephony.SmsMessage;

public class SmsCounter {

    public SmsCount getSmsDeets(CharSequence text) {
        int[] results = SmsMessage.calculateLength(text, false);
        int length = results[1] + results[2];
        return new SmsCount(results[1], length);
    }

    public static class SmsCount {

        public final int usedCharacters;
        public final int messageLength;

        public SmsCount(int usedCharacters, int messageLength) {
            this.usedCharacters = usedCharacters;
            this.messageLength = messageLength;
        }

        @Override
        public String toString() {
            return String.format("Used %1$d characters of a total %2$d", usedCharacters, messageLength);
        }
    }
}

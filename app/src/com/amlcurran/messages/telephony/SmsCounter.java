package com.amlcurran.messages.telephony;

import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsCounter {

    public static final String TAG = SmsCounter.class.getSimpleName();

    private final SmsManager smsManager;
    private CharSequence text;

    public SmsCounter() {
        smsManager = SmsManager.getDefault();
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public int getMessageLength() {
        int[] results = SmsMessage.calculateLength(text, false);
        int numSms = results[0];
        int length = results[1] + results[2];
            Log.d(TAG, "Message length is " + length);
//        List<String> split = smsManager.divideMessage(String.valueOf(text));
//        if (split.size() != 0) {
//            int remainder = text.length() % split.size();
//            int length = (int) ((text.length() - remainder) / (float) split.size());
//            Log.d(TAG, "Message length is " + length);
//            if (length > 0) {
//                return length;
//            }
//        }
        return length;
    }
}

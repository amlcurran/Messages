package com.amlcurran.messages;

import android.util.Log;

import com.amlcurran.messages.core.data.SmsMessage;

import java.util.HashMap;
import java.util.Map;

public class MessagesLog {

    private static final Map<String, Long> timing = new HashMap<>();

    public static void d(Object object, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(object.getClass().getSimpleName(), message);
        }
    }

    public static void d(Object object, String message, Object... objects) {
        d(object, String.format(message, objects));
    }

    public static void w(Object object, String message) {
        Log.w(object.getClass().getSimpleName(), message);
    }

    public static void e(Object object, Throwable throwable) {
        e(object, throwable.getLocalizedMessage());
    }

    public static void e(Object object, String message) {
        Log.e(object.getClass().getSimpleName(), message);
    }

    public static String format(SmsMessage message) {
        if (BuildConfig.DEBUG) {
            return String.format("From: %1$s\nMessage: %2$s", message.getAddress(), message.getBody());
        } else {
            return "Message contents hidden for security.";
        }
    }

    public static void timeStart(String tag) {
        timing.put(tag, System.currentTimeMillis());
    }

    public static void timeEnd(String tag) {
        long start = timing.get(tag);
        d("Timing", String.format("%1$s took %2$d millis", tag, System.currentTimeMillis() - start));
    }
}

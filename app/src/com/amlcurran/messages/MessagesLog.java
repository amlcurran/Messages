package com.amlcurran.messages;

import android.util.Log;

import com.amlcurran.messages.core.data.SmsMessage;

public class MessagesLog {

    public static void d(Object object, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(object.getClass().getSimpleName(), message);
        }
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
}

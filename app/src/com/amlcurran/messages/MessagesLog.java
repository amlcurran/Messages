package com.amlcurran.messages;

import android.util.Log;

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
}

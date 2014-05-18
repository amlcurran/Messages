package com.amlcurran.messages;

import android.content.Context;
import android.provider.Telephony;

public class DefaultAppChecker {

    public interface Callback {
        void isDefaultSmsApp();
        void isNotDefaultSmsApp();
    }

    private final Context activity;
    private final Callback callback;

    public DefaultAppChecker(Context context, Callback callback) {
        this.activity = context;
        this.callback = callback;
    }

    public void checkSmsApp() {
        if (isDefaultSmsApp()) {
            callback.isDefaultSmsApp();
        } else {
            callback.isNotDefaultSmsApp();
        }
    }

    public boolean isDefaultSmsApp() {
        return Telephony.Sms.getDefaultSmsPackage(activity).equals(activity.getPackageName());
    }

}

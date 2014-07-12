package com.amlcurran.messages.threads;

import android.os.Bundle;

public class Bundler {
    public static String getString(String key, Bundle bundle) {
        if (bundle != null) {
            return bundle.getString(key);
        }
        return null;
    }
}

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

package com.amlcurran.messages.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.amlcurran.messages.core.preferences.PreferenceListener;

public class SharedPreferenceListener implements PreferenceListener {
    private final Activity activity;
    private final String[] keys;
    private ChangeListener changeListener;

    public SharedPreferenceListener(Activity activity, String... keys) {
        this.activity = activity;
        this.keys = keys;
    }

    @Override
    public void startListening(ChangeListener listener) {
        changeListener = listener;
        PreferenceManager.getDefaultSharedPreferences(activity)
                .registerOnSharedPreferenceChangeListener(onChangeListener);
    }

    @Override
    public void stopListening() {
        changeListener = null;
        PreferenceManager.getDefaultSharedPreferences(activity)
                .unregisterOnSharedPreferenceChangeListener(onChangeListener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener onChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            for (String requestKey : keys) {
                if (key.equals(requestKey)) {
                    changeListener.preferenceChanged(requestKey);
                }
            }
        }
    };

}

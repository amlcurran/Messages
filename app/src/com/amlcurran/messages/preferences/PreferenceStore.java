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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.amlcurran.messages.core.TextUtils;
import com.amlcurran.messages.core.data.Sort;

import java.util.ArrayList;
import java.util.List;

public class PreferenceStore {

    static final String UNREAD_PRIORITY = "unread_priority";
    static final String RINGTONE = "ringtone";
    static final String NOTIFICATIONS = "notifications";
    static final String SHOWN_ALPHA_MESSAGE = "alpha_message";
    public static final String PERSISTENT_NOTIFICATION = "persistent_notification";
    private final SharedPreferences preferences;
    private final List<PreferenceChangedListener> changedListenerList = new ArrayList<PreferenceChangedListener>();
    private final PreferenceStoreDraftRepository draftRepository;

    public PreferenceStore(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        draftRepository = new PreferenceStoreDraftRepository(context);
    }

    public Sort getConversationSort() {
        boolean sort = preferences.getBoolean(UNREAD_PRIORITY, false);
        return sort ? Sort.UNREAD : Sort.DEFAULT;
    }

    public Uri getRingtoneUri() {
        String ringtone = preferences.getString(RINGTONE, null);
        return TextUtils.isEmpty(ringtone) ? null : Uri.parse(ringtone);
    }

    public boolean showNotifications() {
        return preferences.getBoolean(NOTIFICATIONS, true);
    }

    public void stopListeningToPreferenceChanges(PreferenceChangedListener changedListener) {
        changedListenerList.remove(changedListener);
        if (changedListenerList.isEmpty()) {
            unregisterListener();
        }
    }

    public void listenToPreferenceChanges(PreferenceChangedListener changedListener) {
        changedListenerList.add(changedListener);
        if (changedListenerList.size() == 1) {
            registerListener();
        }
    }

    private void registerListener() {
        preferences.registerOnSharedPreferenceChangeListener(internalChangeListener);
    }

    private void unregisterListener() {
        preferences.unregisterOnSharedPreferenceChangeListener(internalChangeListener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener internalChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    notifyListenersOfChange(key);
                }
            };

    private void notifyListenersOfChange(String key) {
        for (PreferenceChangedListener listener : changedListenerList) {
            listener.preferenceChanged(key);
        }
    }

    public boolean hasNotShownAlphaMessage() {
        return preferences.getBoolean(SHOWN_ALPHA_MESSAGE, true);
    }

    public void storeHasShownAlphaMessage() {
        preferences.edit()
                .putBoolean(SHOWN_ALPHA_MESSAGE, false)
                .apply();
    }

    public boolean isNotificationPersistent() {
        return preferences.getBoolean(PERSISTENT_NOTIFICATION, false);
    }

    public interface PreferenceChangedListener {
        void preferenceChanged(String key);
    }

}

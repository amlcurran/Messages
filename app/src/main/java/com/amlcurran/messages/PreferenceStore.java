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

package com.amlcurran.messages;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.amlcurran.messages.core.TextUtils;
import com.amlcurran.messages.core.data.Sort;

public class PreferenceStore {

    private static final String UNREAD_PRIORITY = "unread_priority";
    private static final String RINGTONE = "ringtone";
    private static final String NOTIFICATIONS = "notifications";
    private final SharedPreferences preferences;

    public PreferenceStore(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
}

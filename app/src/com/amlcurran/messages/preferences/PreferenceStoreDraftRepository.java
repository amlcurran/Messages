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
import android.preference.PreferenceManager;

import com.amlcurran.messages.core.TextUtils;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.data.PhoneNumber;

public class PreferenceStoreDraftRepository implements DraftRepository {
    private SharedPreferences preferences;

    public PreferenceStoreDraftRepository(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static String getDraftKey(PhoneNumber phoneNumber) {
        return "draft" + phoneNumber.flatten();
    }

    @Override
    public String getDraft(PhoneNumber address) {
        return preferences.getString(getDraftKey(address), null);
    }

    @Override
    public void storeDraft(PhoneNumber address, String body) {
        preferences.edit()
                .putString(getDraftKey(address), body)
                .apply();
    }

    @Override
    public void clearDraft(PhoneNumber address) {
        preferences.edit()
                .remove(getDraftKey(address))
                .apply();
    }

    @Override
    public boolean hasDraft(PhoneNumber address) {
        return !TextUtils.isEmpty(preferences.getString(getDraftKey(address), null));
    }
}

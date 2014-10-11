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

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.amlcurran.messages.R;

import java.net.URI;

public class RingtoneHelper {
    private final PreferencesFragment preferencesFragment;
    private final SharedPreferenceStore preferencesStore;

    public RingtoneHelper(PreferencesFragment preferencesFragment, SharedPreferenceStore preferencesStore) {
        this.preferencesFragment = preferencesFragment;
        this.preferencesStore = preferencesStore;
    }

    void setUpToneSummary() {
        URI ringtoneUri = preferencesStore.getRingtoneUri();

        String summary;
        if (ringtoneUri != null) {
            Uri androidUri = Uri.parse(ringtoneUri.toString());
            Ringtone current = getCurrentRingtone(androidUri);
            summary = current.getTitle(preferencesFragment.getActivity());
        } else {
            summary = preferencesFragment.getString(R.string.default_str);
        }

        preferencesFragment.findPreference(SharedPreferenceStore.RINGTONE).setSummary(summary);
    }

    Ringtone getCurrentRingtone(Uri ringtoneUri) {
        return RingtoneManager.getRingtone(preferencesFragment.getActivity(), ringtoneUri);
    }
}
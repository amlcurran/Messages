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

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.amlcurran.messages.R;

public class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private PreferenceStore preferencesStore;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferencesStore = new PreferenceStore(getActivity());
        addPreferencesFromResource(R.xml.preferences);
        setUpToneSummary();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setUpToneSummary() {
        Uri ringtoneUri = preferencesStore.getRingtoneUri();

        String summary;
        if (ringtoneUri != null) {
            Ringtone current = getCurrentRingtone(ringtoneUri);
            summary = current.getTitle(getActivity());
        } else {
            summary = getString(R.string.default_str);
        }

        findPreference(PreferenceStore.RINGTONE).setSummary(summary);
    }

    private Ringtone getCurrentRingtone(Uri ringtoneUri) {
        return RingtoneManager.getRingtone(getActivity(), ringtoneUri);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceStore.RINGTONE.equals(key)) {
            setUpToneSummary();
        }
    }
}

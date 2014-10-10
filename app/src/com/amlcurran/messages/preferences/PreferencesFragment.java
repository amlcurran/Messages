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

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.amlcurran.messages.Entitled;
import com.amlcurran.messages.R;

public class PreferencesFragment extends PreferenceFragment implements SharedPreferenceStore.PreferenceChangedListener, Entitled {

    private RingtoneHelper ringtoneHelper;
    private SharedPreferenceStore preferencesStore;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferencesStore = new SharedPreferenceStore(getActivity());
        ringtoneHelper = new RingtoneHelper(this, preferencesStore);

        addPreferencesFromResource(R.xml.preferences);

        ringtoneHelper.setUpToneSummary();
    }

    @Override
    public void onStart() {
        super.onStart();
        preferencesStore.listenToPreferenceChanges(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        preferencesStore.stopListeningToPreferenceChanges(this);
    }

    @Override
    public void preferenceChanged(String key) {
        if (SharedPreferenceStore.RINGTONE.equals(key)) {
            ringtoneHelper.setUpToneSummary();
        }
    }

    @Override
    public int getTitleResource() {
        return R.string.title_preferences;
    }
}

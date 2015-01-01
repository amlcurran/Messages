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

package com.amlcurran.messages.reporting;

import android.app.Activity;

import com.amlcurran.messages.core.preferences.PreferenceStore;

public class UserPreferenceWrappingStatReporter implements StatReporter {
    private final StatReporter statReporter;
    private final PreferenceStore preferenceStore;

    public UserPreferenceWrappingStatReporter(StatReporter statReporter, PreferenceStore preferenceStore) {
        this.statReporter = statReporter;
        this.preferenceStore = preferenceStore;
    }

    @Override
    public void sendUiEvent(String label) {
        if (preferenceStore.shouldSendStats()) {
            statReporter.sendUiEvent(label);
        }
    }

    @Override
    public void start(Activity activity) {
        statReporter.start(activity);
    }

    @Override
    public void stop(Activity activity) {
        statReporter.stop(activity);
    }

    @Override
    public void sendEvent(String label) {
        if (preferenceStore.shouldSendStats()) {
            statReporter.sendEvent(label);
        }
    }
}

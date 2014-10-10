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

package com.amlcurran.messages.threads;

import android.content.res.Resources;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.analysis.DifferenceStringProvider;

public class ResourcesDifferencesStringProvider implements DifferenceStringProvider {
    private final Resources resources;

    public ResourcesDifferencesStringProvider(Resources resources) {
        this.resources = resources;
    }

    @Override
    public String hoursDifference(long hoursDifference) {
        return resources.getQuantityString(R.plurals.hoursDifference, (int) hoursDifference, hoursDifference);
    }

    @Override
    public String minutesDifference(long minutesDifference) {
        return resources.getQuantityString(R.plurals.minutesDifference, (int) minutesDifference, minutesDifference);
    }

    @Override
    public String underAMinute() {
        return resources.getString(R.string.underAMinuteAgo);
    }

    @Override
    public String yesterday(String time) {
        return resources.getString(R.string.yesterday, time);
    }
}

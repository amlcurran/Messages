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

package com.amlcurran.messages.transition;

import com.amlcurran.messages.ActivityController;
import com.amlcurran.messages.reporting.StatReporter;
import com.amlcurran.messages.ui.control.FragmentController;

public class TransitionManager {
    private final FragmentController fragmentController;
    private final ActivityController activityController;
    private final StatReporter statReporter;

    public TransitionManager(FragmentController fragmentController, ActivityController activityController, StatReporter statReporter) {
        this.fragmentController = fragmentController;
        this.activityController = activityController;
        this.statReporter = statReporter;
    }

    public void toAbout() {
        statReporter.sendUiEvent("about");
        activityController.showAbout();
    }

    public void toPreferences() {
        statReporter.sendUiEvent("preferences");
        activityController.showPreferences();
    }

    public int getView() {
        return fragmentController.getLayoutResourceId();
    }

    public boolean backPressed() {
        return fragmentController.backPressed();
    }

    public TransitionAnchor to() {
        return new FragmentTransitionAnchor(this, fragmentController, activityController, statReporter);
    }

    public TransitionAnchor startAt() {
        return new FragmentTransitionAnchor(this, fragmentController, activityController, statReporter);
    }

    public interface Provider {
        TransitionManager getTransitionManager();
    }
}

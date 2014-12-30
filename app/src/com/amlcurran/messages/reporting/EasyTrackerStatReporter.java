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

import com.amlcurran.messages.MessagesActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class EasyTrackerStatReporter implements StatReporter {

    private final MessagesActivity messagesActivity;
    private final EasyTracker tracker;

    public EasyTrackerStatReporter(MessagesActivity messagesActivity, EasyTracker tracker) {
        this.messagesActivity = messagesActivity;
        this.tracker = tracker;
    }

    @Override
    public void sendUiEvent(String label) {
        tracker.send(MapBuilder.createEvent("ui_action", "button_press", label, null).build());
    }

    @Override
    public void start(Activity activity) {
        tracker.activityStart(messagesActivity);
    }

    @Override
    public void stop(Activity activity) {
        tracker.activityStop(messagesActivity);
    }

    @Override
    public void sendEvent(String label) {
        tracker.send(MapBuilder.createEvent("milestone", "trigger", label, null).build());
    }
}

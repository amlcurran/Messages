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

package com.amlcurran.messages.ui.actionbar;

import android.view.View;

import com.amlcurran.messages.ui.control.HeaderCreationCallback;

public class ActionBarHeaderCallback implements HeaderCreationCallback {

    private final HoloActionBarController actionBarController;

    public ActionBarHeaderCallback(HoloActionBarController actionBarController) {
        this.actionBarController = actionBarController;
    }

    @Override
    public void removeCustomHeader() {
        actionBarController.removeHeader();
        actionBarController.hideHeader();
    }

    @Override
    public void addCustomHeader(View headerView) {
        actionBarController.addHeader(headerView);
        actionBarController.showHeader();
    }
}

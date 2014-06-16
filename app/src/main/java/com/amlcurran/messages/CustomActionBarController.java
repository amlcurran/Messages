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

import android.view.Menu;
import android.view.View;

import com.amlcurran.messages.ui.CustomActionBarView;

public class CustomActionBarController {
    private final CustomActionBarView actionBarView;

    public CustomActionBarController(CustomActionBarView actionBarView) {
        this.actionBarView = actionBarView;
    }

    public void menuCreated(Menu menu) {
        actionBarView.setMenu(menu);
    }

    public void prepareMenu(Menu menu) {
        actionBarView.setMenu(menu);
    }

    public void secondaryVisibility(float slideOffset) {
        actionBarView.setSecondaryVisibility(slideOffset);
    }

    public void removeCustomHeader() {
        actionBarView.removeCustomHeader();
    }

    public void addCustomHeader(View headerView) {
        actionBarView.addCustomHeader(headerView);
    }
}

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

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import com.amlcurran.messages.transition.TransitionManager;
import com.amlcurran.messages.ui.OnOptionsItemSelectedListener;
import com.espian.utils.ui.MenuFinder;

import static com.espian.utils.Verbose.not;

class MenuController implements OnOptionsItemSelectedListener {
    private final Activity activity;
    private final TransitionManager transitionManager;

    public MenuController(Activity activity, TransitionManager transitionManager) {
        this.activity = activity;
        this.transitionManager = transitionManager;
    }

    public boolean create(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.activity_messages, menu);
        return true;
    }

    boolean prepare(Menu menu, boolean isSecondaryVisible) {
        int[] detailRes = new int[] { R.id.menu_call };
        int[] masterRes = new int[] { };
        for (int menuRes : detailRes) {
            MenuItem item = MenuFinder.findItemById(menu, menuRes);
            item.setVisible(isSecondaryVisible);
        }
        for (int menuRes : masterRes) {
            MenuItem item = MenuFinder.findItemById(menu, menuRes);
            item.setVisible(not(isSecondaryVisible));
        }
        return true;
    }

    boolean itemSelected(int item) {
        switch (item) {

            case R.id.action_settings:
                transitionManager.toPreferences();
                return true;

            case R.id.action_about:
                transitionManager.toAbout();
                return true;

        }
        return false;
    }

    public void update() {
        activity.invalidateOptionsMenu();
    }

}

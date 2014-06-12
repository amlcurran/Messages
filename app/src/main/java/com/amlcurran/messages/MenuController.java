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

import com.amlcurran.messages.ui.CustomActionBarView;
import com.amlcurran.messages.ui.OnOptionsItemSelectedListener;
import com.espian.utils.ui.MenuFinder;

public class MenuController implements OnOptionsItemSelectedListener {
    private final Activity activity;
    private final Callbacks callbacks;
    private final CustomActionBarView actionBarView;

    public MenuController(Activity activity, Callbacks callbacks, CustomActionBarView actionBarView) {
        this.activity = activity;
        this.callbacks = callbacks;
        this.actionBarView = actionBarView;
        this.actionBarView.setOnOptionsItemSelectedListener(this);
    }

    public boolean create(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.activity_messages, menu);
        actionBarView.setMenu(menu);
        return true;
    }

    boolean prepare(Menu menu, boolean isSecondaryVisible) {
        int[] detailRes = new int[]{R.id.menu_call};
        int[] masterRes = new int[]{R.id.action_new_message};
        for (int menuRes : detailRes) {
            MenuItem item = MenuFinder.findItemById(menu, menuRes);
            item.setVisible(isSecondaryVisible);
        }
        for (int menuRes : masterRes) {
            MenuItem item = MenuFinder.findItemById(menu, menuRes);
            item.setVisible(!isSecondaryVisible);
        }
        actionBarView.setMenu(menu);
        return true;
    }

    boolean itemSelected(int item) {
        switch (item) {

            case R.id.action_settings:
                callbacks.showSettings();
                return true;

            case R.id.action_about:
                callbacks.showAbout();
                return true;

            case android.R.id.home:
                callbacks.showConversationList();
                return true;

            case R.id.action_new_message:
                callbacks.composeNewMessage();
                return true;

        }
        return false;
    }

    public void update() {
        //actionBarView.updateSelf();
        activity.invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return itemSelected(menuItem.getItemId());
    }

    public static interface Callbacks {
        void showSettings();

        void showAbout();

        void showConversationList();

        void composeNewMessage();
    }
}

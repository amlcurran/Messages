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

package com.amlcurran.messages.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.MenuItem;

import com.amlcurran.messages.ComposeNewFragment;
import com.amlcurran.messages.EmptyFragment;
import com.amlcurran.messages.preferences.PreferencesFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationListFragment;

public class MasterDetailFragmentController implements FragmentController {

    private final Activity activity;
    private final Callback callback;

    public MasterDetailFragmentController(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    public void loadMessagesListFragment() {
        activity.getFragmentManager().beginTransaction()
                .add(R.id.container, new ConversationListFragment())
                .commit();
        callback.insertedMaster();
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToStack) {
        replaceFragmentInternal(fragment, addToStack);
    }

    @Override
    public void loadEmptyFragment() {
        replaceFragmentInternal(new EmptyFragment(), false);
    }

    @Override
    public void showSettings() {
        replaceFragmentInternal(new PreferencesFragment(), true);
    }

    @Override
    public void loadComposeNewFragment() {
        replaceFragmentInternal(new ComposeNewFragment(), false);
    }

    private void replaceFragmentInternal(Fragment fragment, boolean addToStack) {
        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction()
                .replace(R.id.secondary, fragment);
        if (addToStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        callback.insertedDetail();
    }

    @Override
    public boolean optionsItemSelected(MenuItem item) {
        return activity.getFragmentManager().findFragmentById(R.id.secondary).onOptionsItemSelected(item);
    }
}

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
import android.content.Context;
import android.view.MenuItem;

import com.amlcurran.messages.ComposeNewFragment;
import com.amlcurran.messages.EmptyFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.preferences.PreferencesFragment;

public class MasterDetailFragmentController implements FragmentController {

    private final Activity activity;
    private final Callback callback;
    private ViewController viewController;

    public MasterDetailFragmentController(Activity activity, Callback callback, ViewController viewController) {
        this.activity = activity;
        this.callback = callback;
        this.viewController = viewController;
    }

    @Override
    public void loadMessagesListFragment() {
        if (doesNotHaveMessagesList()) {
            activity.getFragmentManager().beginTransaction()
                    .add(viewController.getMasterFrameId(), new ConversationListFragment())
                    .commit();
            callback.insertedMaster();
        }
    }

    private boolean doesNotHaveMessagesList() {
        Fragment fragment = activity.getFragmentManager().findFragmentById(R.id.container);
        return fragment == null || !(fragment instanceof ConversationListFragment);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToStack) {
        replaceFragmentInternal(fragment);
    }

    @Override
    public void loadEmptyFragment() {
        replaceFragmentInternal(new EmptyFragment());
    }

    @Override
    public void showSettings() {
        replaceFragmentInternal(new PreferencesFragment());
    }

    @Override
    public void loadComposeNewFragment() {
        replaceFragmentInternal(new ComposeNewFragment());
    }

    private void replaceFragmentInternal(Fragment fragment) {

        if (fragment instanceof CustomHeaderFragment) {
            Context themedContext = ThemeHelper.getThemedContext(activity);
            callback.addCustomHeader(((CustomHeaderFragment) fragment).getHeaderView(themedContext));
        } else {
            callback.removeCustomHeader();
        }

        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction()
                .replace(viewController.getSecondaryFrameId(), fragment);

        if (viewController.shouldPlaceOnBackStack()) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
        callback.insertedDetail();
    }

    @Override
    public boolean optionsItemSelected(MenuItem item) {
        return activity.getFragmentManager().findFragmentById(viewController.getSecondaryFrameId()).onOptionsItemSelected(item);
    }
}

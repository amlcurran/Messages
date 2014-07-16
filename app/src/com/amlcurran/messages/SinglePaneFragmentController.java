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
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.MenuItem;

import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.preferences.PreferencesFragment;
import com.amlcurran.messages.ui.CustomHeaderFragment;
import com.amlcurran.messages.ui.FragmentController;
import com.amlcurran.messages.ui.ThemeHelper;
import com.amlcurran.messages.ui.ViewController;

public class SinglePaneFragmentController implements FragmentController {

    private final FragmentManager fragmentManager;
    private final ViewController viewController;
    private final Activity activity;
    private final Callback callback;

    public SinglePaneFragmentController(FragmentManager fragmentManager, ViewController viewController, Activity activity, Callback callback) {
        this.fragmentManager = fragmentManager;
        this.viewController = viewController;
        this.activity = activity;
        this.callback = callback;
        bindBackstackListener(viewController, activity, callback);
    }

    private void bindBackstackListener(final ViewController viewController, final Activity activity, final Callback callback) {
        activity.getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = activity.getFragmentManager().findFragmentById(viewController.getSecondaryFrameId());
                handleCustomHeader(currentFragment, activity, callback);
                // This is a hack to force the new messages button to show
                if (currentFragment instanceof ConversationListFragment) {
                    callback.insertedMaster();
                }
            }
        });
    }

    private void handleCustomHeader(Fragment currentFragment, Activity activity, Callback callback) {
        if (currentFragment instanceof CustomHeaderFragment) {
            Context themedContext = ThemeHelper.getThemedContext(activity);
            callback.addCustomHeader(((CustomHeaderFragment) currentFragment).getHeaderView(themedContext));
        } else {
            callback.removeCustomHeader();
        }
    }

    @Override
    public void loadConversationListFragment() {
        createTransaction()
                .replace(viewController.getMasterFrameId(), new ConversationListFragment())
                .commit();
    }

    private FragmentTransaction createTransaction() {
        return fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToStack) {
        replaceFragmentInternal(fragment);
    }

    private void replaceFragmentInternal(Fragment fragment) {

        handleCustomHeader(fragment, activity, callback);

        FragmentTransaction transaction = createTransaction()
                .replace(viewController.getSecondaryFrameId(), fragment);

        if (viewController.shouldPlaceOnBackStack()) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
        callback.insertedDetail();
    }

    @Override
    public void loadEmptyFragment() {
        // Don't do anything
    }

    @Override
    public void showSettings() {
        replaceFragmentInternal(new PreferencesFragment());
    }

    @Override
    public void loadComposeNewFragment() {
        replaceFragmentInternal(new ComposeNewFragment());
    }

    @Override
    public boolean optionsItemSelected(MenuItem item) {
        return false;
    }
}

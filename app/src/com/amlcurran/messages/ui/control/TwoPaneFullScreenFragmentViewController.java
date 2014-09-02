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

package com.amlcurran.messages.ui.control;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;

import com.amlcurran.messages.ComposeNewFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.preferences.PreferencesFragment;
import com.amlcurran.messages.ui.CustomHeaderFragment;
import com.amlcurran.messages.ui.ThemeHelper;

public class TwoPaneFullScreenFragmentViewController extends BaseViewController implements FragmentController {

    private final FragmentManager fragmentManager;
    private final Activity activity;
    private final FragmentCallback fragmentCallback;

    public TwoPaneFullScreenFragmentViewController(Activity activity, FragmentCallback fragmentCallback, ViewCallback viewCallback) {
        super(viewCallback);
        this.fragmentManager = activity.getFragmentManager();
        this.activity = activity;
        this.fragmentCallback = fragmentCallback;
        bindBackstackListener(activity, fragmentCallback);
    }

    private void bindBackstackListener(final Activity activity, final FragmentCallback fragmentCallback) {
        activity.getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = activity.getFragmentManager().findFragmentById(getSecondaryFrameId());
                handleCustomHeader(currentFragment, activity, fragmentCallback);
                // This is a hack to force the new messages button to show
                if (currentFragment instanceof ConversationListFragment) {
                    fragmentCallback.insertedMaster();
                    hideSecondary();
                } else {
                    fragmentCallback.insertedDetail();
                    showSecondary();
                }
                if (currentFragment instanceof CustomHeaderFragment) {
                    viewCallback.secondaryVisible();
                } else {
                    viewCallback.secondaryHidden();
                }
            }
        });
    }

    private void handleCustomHeader(Fragment currentFragment, Activity activity, FragmentCallback fragmentCallback) {
        if (currentFragment instanceof CustomHeaderFragment) {
            Context themedContext = ThemeHelper.getThemedContext(activity);
            fragmentCallback.addCustomHeader(((CustomHeaderFragment) currentFragment).getHeaderView(themedContext));
        } else {
            fragmentCallback.removeCustomHeader();
        }
    }

    @Override
    public void loadConversationListFragment() {
        if (frameIsEmpty(getMasterFrameId())) {
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(getMasterFrameId(), new ConversationListFragment())
                    .commit();
        }
    }

    private boolean frameIsEmpty(int frameId) {
        return fragmentManager.findFragmentById(frameId) == null;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToStack) {
        replaceFragmentInternal(fragment);
    }

    private void replaceFragmentInternal(Fragment fragment) {
        buildReplaceFragmentTransaction(fragment)
                .setCustomAnimations(R.animator.fade_in_quick, 0)
                .commit();
        fragmentCallback.insertedDetail();
        showSecondary();
        handleCustomHeader(fragment, activity, fragmentCallback);
    }

    private FragmentTransaction buildReplaceFragmentTransaction(Fragment fragment) {
        handleCustomHeader(fragment, activity, fragmentCallback);

        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(getSecondaryFrameId(), fragment);

        if (shouldPlaceOnBackStack()) {
            transaction.addToBackStack(null);
        }
        return transaction;
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

    @Override
    public void attachedFragment(Fragment fragment) {

    }

    @Override
    public boolean backPressed() {
        if (activity.findViewById(getSecondaryFrameId()).getVisibility() == View.VISIBLE) {
            hideSecondary();
            fragmentCallback.insertedMaster();
            return true;
        }
        return false;
    }

    private void hideSecondary() {
        activity.findViewById(getSecondaryFrameId()).setVisibility(View.GONE);
        activity.findViewById(getMasterFrameId()).setVisibility(View.VISIBLE);
        fragmentCallback.removeCustomHeader();
        viewCallback.secondaryHidden();
    }

    private void showSecondary() {
        activity.findViewById(getSecondaryFrameId()).setVisibility(View.VISIBLE);
        activity.findViewById(getMasterFrameId()).setVisibility(View.GONE);
        viewCallback.secondaryVisible();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_messages;
    }

    @Override
    protected void initView(Activity activity) {
    }

    @Override
    public int getMasterFrameId() {
        return R.id.container;
    }

    @Override
    public int getSecondaryFrameId() {
        return R.id.secondary;
    }

    @Override
    public boolean shouldPlaceOnBackStack() {
        return false;
    }
}

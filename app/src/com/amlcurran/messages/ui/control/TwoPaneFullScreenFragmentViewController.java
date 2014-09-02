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
import android.view.View;

import com.amlcurran.messages.ComposeNewFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.ui.CustomHeaderFragment;
import com.amlcurran.messages.ui.ThemeHelper;

public class TwoPaneFullScreenFragmentViewController implements FragmentController {

    private final FragmentCallback fragmentCallback;
    private final FragmentManager fragmentManager;
    private final Activity activity;

    public TwoPaneFullScreenFragmentViewController(Activity activity, FragmentCallback fragmentCallback) {
        this.fragmentManager = activity.getFragmentManager();
        this.activity = activity;
        this.fragmentCallback = fragmentCallback;
    }

    private void handleCustomHeader(Fragment currentFragment) {
        if (currentFragment instanceof CustomHeaderFragment) {
            Context themedContext = ThemeHelper.getThemedContext(activity);
            fragmentCallback.addCustomHeader(((CustomHeaderFragment) currentFragment).getHeaderView(themedContext));
        } else {
            fragmentCallback.removeCustomHeader();
        }
    }

    @Override
    public void loadConversationListFragment() {
        putFragment(new ConversationListFragment());
    }

    @Override
    public void putFragment(Fragment fragment) {
        if (fragment instanceof Master) {
            insertMasterFragment(fragment);
        } else {
            insertContentFragment(fragment);
        }
        if (fragment instanceof CustomHeaderFragment) {
            fragmentCallback.addCustomHeader(((CustomHeaderFragment) fragment).getHeaderView(activity));
        } else {
            fragmentCallback.removeCustomHeader();
        }
    }

    private void insertMasterFragment(Fragment fragment) {
        if (frameIsEmpty(getMasterFrameId())) {
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(getMasterFrameId(), fragment)
                    .commit();
            fragmentCallback.insertedMaster();
            hideSecondary();
        }
    }

    private void insertContentFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(getSecondaryFrameId(), fragment)
                .setCustomAnimations(R.animator.fade_in_quick, 0)
                .commit();
        fragmentCallback.insertedDetail();
        showSecondary();
    }

    private boolean frameIsEmpty(int frameId) {
        return fragmentManager.findFragmentById(frameId) == null;
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        putFragment(fragment);
    }

    @Override
    public void loadComposeNewFragment() {
        putFragment(new ComposeNewFragment());
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
        fragmentCallback.secondaryHidden();
    }

    private void showSecondary() {
        activity.findViewById(getSecondaryFrameId()).setVisibility(View.VISIBLE);
        activity.findViewById(getMasterFrameId()).setVisibility(View.GONE);
        fragmentCallback.secondaryVisible();
    }

    private int getMasterFrameId() {
        return R.id.container;
    }

    private int getSecondaryFrameId() {
        return R.id.secondary;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_messages;
    }
}

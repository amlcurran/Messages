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

import com.amlcurran.messages.newcompose.ComposeNewFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.ui.CustomHeaderFragment;

public class SinglePaneFullScreenFragmentViewController implements FragmentController {

    private final FragmentCallback fragmentCallback;
    private final FragmentManager fragmentManager;
    private final Activity activity;
    private final HeaderCreationCallback headerCreationCallback;

    public SinglePaneFullScreenFragmentViewController(Activity activity, FragmentCallback fragmentCallback, HeaderCreationCallback headerCreationCallback) {
        this.fragmentManager = activity.getFragmentManager();
        this.activity = activity;
        this.fragmentCallback = fragmentCallback;
        this.headerCreationCallback = headerCreationCallback;
        this.fragmentManager.addOnBackStackChangedListener(new DefaultBackStackListener());
    }

    @Override
    public void loadConversationListFragment() {
        putFragment(new ConversationListFragment());
    }

    @Override
    public void putFragment(Fragment fragment) {
        putFragmentInternal(fragment, R.animator.fade_in_quick);
    }

    private void putFragmentInternal(Fragment fragment, int inAnimation) {
        if (fragment instanceof Master) {
            insertMasterFragment(fragment);
        } else {
            insertContentFragmentInternal(fragment, inAnimation);
        }
    }

    private void insertMasterFragment(Fragment fragment) {
        if (frameIsEmpty(getFrameId())) {
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(getFrameId(), fragment)
                    .commit();
            insertedMaster();
        }
    }

    private void insertedMaster() {
        fragmentCallback.insertedMaster();
        fragmentCallback.secondaryHidden();
    }

    private void insertContentFragmentInternal(Fragment fragment, int inAnimation) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(inAnimation, 0)
                .replace(getFrameId(), fragment)
                .addToBackStack(null)
                .commit();
        insertedContent();
    }

    private void insertedContent() {
        fragmentCallback.insertedDetail();
        fragmentCallback.secondaryVisible();
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
        putFragmentInternal(new ComposeNewFragment(), R.animator.new_compose_in);
    }

    @Override
    public boolean backPressed() {
        return false;
    }

    private int getFrameId() {
        return R.id.container;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_messages;
    }

    private class DefaultBackStackListener implements FragmentManager.OnBackStackChangedListener {

        @Override
        public void onBackStackChanged() {
            Fragment fragment = fragmentManager.findFragmentById(getFrameId());
            if (fragment instanceof Master) {
                insertedMaster();
            } else {
                insertedContent();
            }
            if (fragment instanceof CustomHeaderFragment) {
                headerCreationCallback.addCustomHeader(((CustomHeaderFragment) fragment).getHeaderView(activity));
            } else {
                headerCreationCallback.removeCustomHeader();
            }
        }

    }
}

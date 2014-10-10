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
import android.view.View;

import com.amlcurran.messages.newcompose.ComposeNewFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.ui.CustomHeaderFragment;

public class TwoPaneFullScreenFragmentViewController implements FragmentController {

    private final FragmentCallback fragmentCallback;
    private final FragmentManager fragmentManager;
    private final Activity activity;
    private final HeaderCreationCallback headerCreationCallback;

    public TwoPaneFullScreenFragmentViewController(Activity activity, FragmentCallback fragmentCallback, HeaderCreationCallback headerCreationCallback) {
        this.fragmentManager = activity.getFragmentManager();
        this.activity = activity;
        this.fragmentCallback = fragmentCallback;
        this.headerCreationCallback = headerCreationCallback;
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
        if (fragment instanceof CustomHeaderFragment) {
            headerCreationCallback.addCustomHeader(((CustomHeaderFragment) fragment).getHeaderView(activity));
        } else {
            headerCreationCallback.removeCustomHeader();
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

    private void insertContentFragmentInternal(Fragment fragment, int inAnimation) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(inAnimation, 0)
                .replace(getSecondaryFrameId(), fragment)
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
        putFragmentInternal(new ComposeNewFragment(), R.animator.new_compose_in);
    }

    @Override
    public boolean backPressed() {
        if (activity.findViewById(getSecondaryFrameId()).getVisibility() == View.VISIBLE) {
            removeContent();
            hideSecondary();
            fragmentCallback.insertedMaster();
            return true;
        }
        return false;
    }

    private void removeContent() {
        Fragment currentContent = fragmentManager.findFragmentById(getSecondaryFrameId());
        if (currentContent != null) {
            fragmentManager.beginTransaction()
                    .remove(currentContent)
                    .commit();
        }
    }

    private void hideSecondary() {
        activity.findViewById(getSecondaryFrameId()).setVisibility(View.GONE);
        activity.findViewById(getMasterFrameId()).setVisibility(View.VISIBLE);
        headerCreationCallback.removeCustomHeader();
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

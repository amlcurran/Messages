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
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.MenuItem;
import android.view.View;

import com.amlcurran.messages.ComposeNewFragment;
import com.amlcurran.messages.EmptyFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.preferences.PreferencesFragment;
import com.amlcurran.messages.ui.CustomHeaderFragment;
import com.amlcurran.messages.ui.MasterFragment;
import com.amlcurran.messages.ui.ThemeHelper;

public class SlidingPaneFragmentViewController extends BaseViewController implements FragmentController {

    private final Activity activity;
    private final FragmentCallback fragmentCallback;
    private SlidingPaneLayout slider;
    private boolean isSecondaryOpen = false;

    public SlidingPaneFragmentViewController(final Activity activity, final FragmentCallback fragmentCallback, final ViewCallback viewCallback) {
        super(viewCallback);
        this.activity = activity;
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public void loadConversationListFragment() {
        if (doesNotHaveMessagesList()) {
            activity.getFragmentManager().beginTransaction()
                    .add(getMasterFrameId(), new ConversationListFragment())
                    .commit();
            fragmentCallback.insertedMaster();
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

        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction()
                .replace(getSecondaryFrameId(), fragment);

        if (shouldPlaceOnBackStack()) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
        fragmentCallback.insertedDetail();
    }

    private void getHeaderView(Fragment fragment) {
        if (fragment instanceof CustomHeaderFragment) {
            Context themedContext = ThemeHelper.getThemedContext(activity);
            fragmentCallback.addCustomHeader(((CustomHeaderFragment) fragment).getHeaderView(themedContext));
        } else {
            fragmentCallback.removeCustomHeader();
        }
    }

    @Override
    public boolean optionsItemSelected(MenuItem item) {
        return activity.getFragmentManager().findFragmentById(getSecondaryFrameId()).onOptionsItemSelected(item);
    }

    @Override
    public void attachedFragment(Fragment fragment) {
        if (fragmentIsContent(fragment)) {
            getHeaderView(fragment);
        }
    }

    private boolean fragmentIsContent(Fragment fragment) {
        return !(fragment instanceof MasterFragment);
    }

    @Override
    public boolean backPressed() {
        if (!slider.isOpen()) {
            slider.openPane();
            return true;
        }
        return false;
    }

    @Override
    public void hideSecondary() {
        slider.openPane();
    }

    @Override
    public void showSecondary() {
        slider.closePane();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_messages_sliding;
    }

    @Override
    protected void initView(Activity activity) {
        slider = (SlidingPaneLayout) activity.findViewById(R.id.sliding_pane);
        slider.setParallaxDistance((int) activity.getResources().getDimension(R.dimen.slider_parallax));
        slider.setShadowResource(R.drawable.slider_shadow);
        slider.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                viewCallback.secondarySliding(slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {
                if (isSecondaryOpen) {
                    viewCallback.secondaryHidden();
                    isSecondaryOpen = false;
                }
            }

            @Override
            public void onPanelClosed(View panel) {
                if (!isSecondaryOpen) {
                    viewCallback.secondaryVisible();
                    isSecondaryOpen = true;
                }
            }
        });
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

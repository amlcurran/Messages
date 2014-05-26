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
import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;

import com.amlcurran.messages.MessagesActivity;
import com.amlcurran.messages.PreferencesFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationListFragment;

public class SlidingPaneUiController implements UiController {

    private final Activity activity;
    private final UiCallback uiCallback;
    private SlidingPaneLayout slider;
    private View disabledBanner;

    public SlidingPaneUiController(Activity activity, UiCallback uiCallback) {
        this.activity = activity;
        this.uiCallback = uiCallback;
    }

    @Override
    public void loadMessagesListFragment() {
        activity.getFragmentManager().beginTransaction()
                .add(R.id.container, new ConversationListFragment())
                .commit();
        slider.openPane();
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToStack) {
        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction()
                .replace(R.id.secondary, fragment);
        if (addToStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        slider.closePane();
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.activity_messages_sliding, null);
        disabledBanner = view.findViewById(R.id.disabled_banner);
        slider = (SlidingPaneLayout) view.findViewById(R.id.sliding_pane);
        slider.setParallaxDistance((int) activity.getResources().getDimension(R.dimen.slider_parallax));
        slider.setShadowResource(R.drawable.slider_shadow);
        slider.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelOpened(View panel) {
                uiCallback.onSecondaryHidden();
            }

            @Override
            public void onPanelClosed(View panel) {
                uiCallback.onSecondaryVisible();
            }
        });
        return view;
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
    public void loadEmptyFragment() {
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.secondary, new MessagesActivity.EmptyFragment())
                .commit();
    }

    @Override
    public void showDisabledBanner() {
        disabledBanner.setVisibility(View.VISIBLE);
    }

    @Override
    public View getDisabledBanner() {
        return disabledBanner;
    }

    @Override
    public void hideSecondary() {
        slider.openPane();
    }

    @Override
    public void showSettings() {
        replaceFragment(new PreferencesFragment(), true);
    }

    @Override
    public void hideDisabledBanner() {
        disabledBanner.setVisibility(View.GONE);
    }

    public interface UiCallback {
        void onSecondaryVisible();
        void onSecondaryHidden();
    }

}

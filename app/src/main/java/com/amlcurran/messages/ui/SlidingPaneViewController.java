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

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.MessagesLoader;

public class SlidingPaneViewController implements ViewController, View.OnClickListener {

    private final Callback callback;
    private final ActionBar actionBar;
    private View disabledBanner;
    private SlidingPaneLayout slider;
    private View newMessageButton;

    public SlidingPaneViewController(Callback callback, ActionBar actionBar) {
        this.callback = callback;
        this.actionBar = actionBar;
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
    public void hideDisabledBanner() {
        disabledBanner.setVisibility(View.GONE);
    }

    @Override
    public void showDisabledBanner() {
        disabledBanner.setVisibility(View.VISIBLE);
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
    public void setContentView(Activity activity) {
        activity.setContentView(R.layout.activity_messages_sliding);
        initView(activity);
    }

    private void initView(Activity activity) {
        disabledBanner = activity.findViewById(R.id.disabled_banner);
        disabledBanner.setOnClickListener(this);
        slider = (SlidingPaneLayout) activity.findViewById(R.id.sliding_pane);
        slider.setParallaxDistance((int) activity.getResources().getDimension(R.dimen.slider_parallax));
        slider.setShadowResource(R.drawable.slider_shadow);
        slider.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //showPersonChip();
                //contactView.setAlpha(1 - slideOffset);
                callback.secondarySliding(slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {
                //hidePersonChip();
                callback.secondaryHidden();
            }

            @Override
            public void onPanelClosed(View panel) {
                //showPersonChip();
                callback.secondaryVisible();
            }
        });
        newMessageButton = activity.findViewById(R.id.button_new_message);
        newMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.newMessageButtonClicked();
            }
        });
    }

    @Override
    public void onClick(View v) {
        callback.defaultsBannerPressed();
    }

    @Override
    public void setUpActionBar() {
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        contactView = new ContactView(actionBar.getThemedContext(), null);
//        contactView.setLayoutParams(layoutParams);
//        actionBar.setCustomView(contactView);
    }

    @Override
    public void showSelectedContact(Contact contact, MessagesLoader messagesLoader) {
        //contactView.setContact(contact, messagesLoader);
        //showPersonChip();
    }

    private void showPersonChip() {
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        //contactView.setAlpha(1);
    }

    private void hidePersonChip() {
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
    }
}

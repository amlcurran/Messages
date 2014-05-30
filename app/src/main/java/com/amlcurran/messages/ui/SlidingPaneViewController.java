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
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.widget.Toast;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Conversation;

import java.util.List;

public class SlidingPaneViewController implements ViewController, View.OnClickListener {

    private final Activity activity;
    private final Callback callback;
    private View disabledBanner;
    private SlidingPaneLayout slider;

    public SlidingPaneViewController(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
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
    public void setContentView() {
        activity.setContentView(R.layout.activity_messages_sliding);
        initView();
    }

    private void initView() {
        disabledBanner = activity.findViewById(R.id.disabled_banner);
        disabledBanner.setOnClickListener(this);
        slider = (SlidingPaneLayout) activity.findViewById(R.id.sliding_pane);
        slider.setParallaxDistance((int) activity.getResources().getDimension(R.dimen.slider_parallax));
        slider.setShadowResource(R.drawable.slider_shadow);
        slider.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelOpened(View panel) {
                callback.secondaryHidden();
            }

            @Override
            public void onPanelClosed(View panel) {
                callback.secondaryVisible();
            }
        });
    }

    @Override
    public void onClick(View v) {
        callback.defaultsBannerPressed();
    }

    @Override
    public void deletedConversations(List<Conversation> deletedConversations) {
        String toast;
        if (deletedConversations.size() == 1) {
            toast = activity.getString(R.string.deleted_one_thread, deletedConversations.get(0).getContact().getDisplayName());
        } else {
            toast = activity.getString(R.string.deleted_many_threads, deletedConversations.size());
        }
        Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
    }
}

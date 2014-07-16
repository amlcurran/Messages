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

import com.amlcurran.messages.R;

public class SlidingPaneViewController extends BaseViewController implements View.OnClickListener {

    private SlidingPaneLayout slider;
    private boolean isSecondaryOpen = false;

    public SlidingPaneViewController(Callback callback) {
        super(callback);
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
                callback.secondarySliding(slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {
                if (isSecondaryOpen) {
                    callback.secondaryHidden();
                    showNewMessageButton();
                    isSecondaryOpen = false;
                }
            }

            @Override
            public void onPanelClosed(View panel) {
                if (!isSecondaryOpen) {
                    callback.secondaryVisible();
                    hideNewMessageButton();
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

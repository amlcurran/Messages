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
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.amlcurran.messages.R;

public abstract class BaseViewController implements ViewController, View.OnClickListener {
    protected final ViewCallback viewCallback;
    private View disabledBanner;
    private View newMessageButton;

    public BaseViewController(ViewCallback viewCallback) {
        this.viewCallback = viewCallback;
    }

    protected void hideNewMessageButton() {
        newMessageButton.animate()
                .translationYBy(newMessageButton.getMeasuredHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(300)
                .start();
    }

    @Override
    public abstract boolean backPressed();

    @Override
    public void hideDisabledBanner() {
        disabledBanner.setVisibility(View.GONE);
    }

    @Override
    public void showDisabledBanner() {
        disabledBanner.setVisibility(View.VISIBLE);
    }

    @Override
    public abstract void hideSecondary();

    @Override
    public abstract void showSecondary();

    @Override
    public void setContentView(Activity activity) {
        activity.setContentView(getLayout());
        initViewInternal(activity);
    }

    public void onClick(View v) {
        viewCallback.defaultsBannerPressed();
    }

    private void initViewInternal(Activity activity) {
        disabledBanner = activity.findViewById(R.id.disabled_banner);
        disabledBanner.setOnClickListener(this);
        newMessageButton = activity.findViewById(R.id.button_new_message);
        newMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCallback.newMessageButtonClicked();
            }
        });
        initView(activity);
    }

    protected abstract int getLayout();

    protected abstract void initView(Activity activity);

    protected void showNewMessageButton() {
        newMessageButton.animate()
                .translationYBy(-newMessageButton.getMeasuredHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(showMessageButtonDuration())
                .start();
    }

    protected int showMessageButtonDuration() {
        return 150;
    }

    public void disableNewMessageButton() {
        newMessageButton.setVisibility(View.GONE);
    }

    public void enableNewMessageButton() {
        newMessageButton.setVisibility(View.VISIBLE);
    }

    @Override
    public abstract int getMasterFrameId();

    @Override
    public abstract int getSecondaryFrameId();

    @Override
    public abstract boolean shouldPlaceOnBackStack();
}

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

import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class NewMessageButtonController {
    private final View newMessageButton;

    public NewMessageButtonController(View newMessageButton, final FragmentController.FragmentCallback viewCallback) {
        this.newMessageButton = newMessageButton;
        this.newMessageButton.setOnClickListener(new NewMessageClickListener(viewCallback));
    }

    protected void hideNewMessageButton() {
        newMessageButton.animate()
                .translationYBy(newMessageButton.getMeasuredHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(300)
                .start();
    }

    protected void showNewMessageButton() {
        newMessageButton.animate()
                .translationYBy(-newMessageButton.getMeasuredHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(300)
                .start();
    }

    public void disableNewMessageButton() {
        newMessageButton.setVisibility(View.GONE);
    }

    public void enableNewMessageButton() {
        newMessageButton.setVisibility(View.VISIBLE);
    }

    private static class NewMessageClickListener implements View.OnClickListener {
        private final FragmentController.FragmentCallback viewCallback;

        public NewMessageClickListener(FragmentController.FragmentCallback viewCallback) {
            this.viewCallback = viewCallback;
        }

        @Override
        public void onClick(View v) {
            viewCallback.newMessageButtonClicked();
        }
    }
}
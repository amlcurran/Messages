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

import android.os.Build;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.amlcurran.messages.R;
import com.amlcurran.messages.reporting.StatReporter;
import com.amlcurran.messages.transition.TransitionManager;

public class NewMessageButtonController {
    private final View newMessageButton;

    public NewMessageButtonController(View newMessageButton, TransitionManager transitionManager, StatReporter statReporter) {
        this.newMessageButton = newMessageButton;
        this.newMessageButton.setOnClickListener(new NewMessageClickListener(transitionManager, statReporter));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.newMessageButton.setOutlineProvider(new CenteredOvalOutlineProvider(newMessageButton.getResources().getDimensionPixelOffset(R.dimen.fab_size)));
            this.newMessageButton.setClipToOutline(true);
        }
    }

    public void hideNewMessageButton() {
        newMessageButton.animate()
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        newMessageButton.setTranslationY(0);
                    }
                })
                .translationY(newMessageButton.getMeasuredHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .setDuration(150)
                .start();
    }

    public void showNewMessageButton() {
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
        private final TransitionManager transitionManager;
        private final StatReporter statReporter;

        public NewMessageClickListener(TransitionManager transitionManager, StatReporter statReporter) {
            this.transitionManager = transitionManager;
            this.statReporter = statReporter;
        }

        @Override
        public void onClick(View v) {
            statReporter.sendUiEvent("new_compose_fab");
            transitionManager.to().newCompose();
        }
    }
}
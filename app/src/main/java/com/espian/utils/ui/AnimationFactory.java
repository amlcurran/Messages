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

package com.espian.utils.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class AnimationFactory {
    public static final float SCALE_LARGE = 1.5f;
    public static final float SCALE_DEFAULT = 1.0f;
    public static final float ALPHA_NONE = 0.0f;
    public static final float ALPHA_FULL = 1.0f;

    public AnimationFactory() {
    }

    public static void fadeScaleIn(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", SCALE_LARGE, SCALE_DEFAULT);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", SCALE_LARGE, SCALE_DEFAULT);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", ALPHA_NONE, ALPHA_FULL);
        view.setAlpha(0);
        view.setVisibility(View.VISIBLE);

        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(scaleX, scaleY, alpha);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }

    public static void fadeScaleOut(final View view, final int endVisibility) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", SCALE_LARGE);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", SCALE_LARGE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", ALPHA_NONE);

        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(scaleX, scaleY, alpha);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(endVisibility);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation.start();
    }

    public static void fadeScaleOut(final View view) {
        fadeScaleOut(view, View.GONE);
    }

    public static boolean viewIsShowing(View keyView) {
        return keyView.getVisibility() == View.VISIBLE;
    }

    public static void alphaIn(final View view) {
        if (view.getAlpha() == 1 || view.getVisibility() == View.VISIBLE) {
            return;
        }

        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", ALPHA_FULL);

        alpha.setInterpolator(new AccelerateDecelerateInterpolator());
        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setAlpha(ALPHA_NONE);
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        alpha.start();
    }
}
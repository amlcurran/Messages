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
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class which allows easy hiding and showing of not yet loaded data.
 */
public class LoadHideHelper {

    private final List<View> mTargets;
    private Interpolator mInterpolator;
    private int mDuration = 300;
    private ProgressBar mProgress;
    private ObjectAnimator mAnimProgressHide, mAnimProgressShow;
    private static boolean sOldWithoutNoa = false;

    static {

        try {
            Class.forName("com.nineoldandroids.animation.Animator");
            sOldWithoutNoa = false;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("android.animation.Animator");
                sOldWithoutNoa = false;
            } catch (ClassNotFoundException e1) {
                sOldWithoutNoa = true;
                Log.w("LoadHideHelper", "Using LoadHideHelper without NineOldAndroids on API <= 10. Animations are disabled.");
            }
        }

    }

    /**
     * Initialise a new LoadHideHelper. Hides the {@link android.view.View} by default.
     *
     * @param target Fragment to hide the view of
     */
    public LoadHideHelper(Fragment target) {
        mTargets = new ArrayList<View>();
        mTargets.add(target.getView());
        initAndHide();
    }

    /**
     * Initialise a new LoadHideHelper. Hides the {@link android.view.View} by default.
     *
     * @param target View to hide
     */
    public LoadHideHelper(View target) {
        mTargets = new ArrayList<View>();
        mTargets.add(target);
        initAndHide();
    }

    /**
     * @param target                target {@link android.view.View} to hide
     * @param indeterminateProgress should the ProgressBar be indeterminate.
     * @throws LoadHideHelper.IllegalParentViewException If the parent of the target is not a {@link android.widget.FrameLayout}
     */
    public LoadHideHelper(View target, boolean indeterminateProgress) {
        // Validate and init
        if (!target.getParent().getClass().equals(FrameLayout.class)) {
            throw new IllegalParentViewException(target.getParent());
        }
        mTargets = new ArrayList<View>();
        mTargets.add(target);

        // Copy the LayoutParams of the target view & set up ProgressBar
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mProgress = new ProgressBar(target.getContext(), null, indeterminateProgress ? android.R.attr.progressBarStyleLarge : android.R.attr.progressBarStyleHorizontal);
        mProgress.setIndeterminate(indeterminateProgress);

        // Set up the Animators for the ProgressBar
        if (!sOldWithoutNoa) {
            mAnimProgressHide = ObjectAnimator.ofFloat(mProgress, "alpha", 1f, 0f);
            mAnimProgressHide.addListener(new EndListener(mProgress));
            mAnimProgressShow = ObjectAnimator.ofFloat(mProgress, "alpha", 0f, 1f);
            mAnimProgressShow.addListener(new StartListener(mProgress));
        }

        // Insert the ProgressBar into the FrameLayout
        ((FrameLayout) target.getParent()).addView(mProgress, params);
        initAndHide();
    }

    /**
     * Initialise a new LoadHideHelper. Hides the {@link android.view.View} by default.
     *
     * @param targets Views to hide
     */
    public LoadHideHelper(View... targets) {
        mTargets = new ArrayList<View>();
        Collections.addAll(mTargets, targets);
        initAndHide();
    }

    private LoadHideHelper initAndHide() {
        for (View view : mTargets) {
            if (sOldWithoutNoa) view.setVisibility(View.INVISIBLE);
            else makeHideAnimation(view).setDuration(0).start();
        }
        return this;
    }

    /**
     * Show the {@link android.view.View}
     */
    public LoadHideHelper show() {
        if (sOldWithoutNoa) showWithoutAnimation();
        else {
            AnimatorSet concurrent = new AnimatorSet();
            List<Animator> animators = new ArrayList<Animator>();
            for (View target : mTargets) {
                animators.add(makeShowAnimation(target));
            }
            concurrent.setInterpolator(mInterpolator);
            if (mProgress != null) animators.add(mAnimProgressHide);
            concurrent.addListener(new StartListener(mTargets));
            concurrent.setDuration(mDuration).playTogether(animators);
            concurrent.start();
        }
        return this;
    }

    public LoadHideHelper showWithoutAnimation() {
        for (View view : mTargets) view.setVisibility(View.VISIBLE);
        if (mProgress != null) mProgress.setVisibility(View.INVISIBLE);
        return this;
    }

    /**
     * Hide the {@link android.view.View}
     */
    public LoadHideHelper hide() {
        if (sOldWithoutNoa) hideWithoutAnimation();
        else {
            AnimatorSet concurrent = new AnimatorSet();
            List<Animator> animators = new ArrayList<Animator>();
            for (View target : mTargets) {
                animators.add(makeHideAnimation(target));
            }
            concurrent.setInterpolator(mInterpolator);
            if (mProgress != null) animators.add(mAnimProgressShow);
            concurrent.addListener(new EndListener(mTargets));
            concurrent.setDuration(mDuration).playTogether(animators);
            concurrent.start();
        }
        return this;
    }

    public LoadHideHelper hideWithoutAnimation() {
        for (View view : mTargets) view.setVisibility(View.INVISIBLE);
        if (mProgress != null) mProgress.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Initialise the animation with which to show items with.
     * Setting a duration here will be overridden.
     *
     * @return
     */
    public Animator makeShowAnimation(View target) {
        return ObjectAnimator.ofFloat(target, "alpha", 1f);
    }

    /**
     * Initialise the animation with which to hide items with.
     * Setting a duration here will be overridden.
     *
     * @return
     */
    public Animator makeHideAnimation(View target) {
        return ObjectAnimator.ofFloat(target, "alpha", 0f);
    }

    public ProgressBar getProgressBar() {
        return mProgress;
    }

    public int getAnimationDuration() {
        return mDuration;
    }

    public void setAnimationDuration(int duration) {
        mDuration = duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    private class EndListener extends AnimatorListenerAdapter {

        private List<View> mViews;

        public EndListener(View view) {
            mViews = new ArrayList<View>();
            mViews.add(view);
        }

        public EndListener(List<View> views) {
            mViews = new ArrayList<View>();
            mViews.addAll(views);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            for (View view : mViews) view.setVisibility(View.INVISIBLE);
        }
    }

    private class StartListener extends AnimatorListenerAdapter {

        private List<View> mViews;

        public StartListener(View view) {
            mViews = new ArrayList<View>();
            mViews.add(view);
        }

        public StartListener(List<View> views) {
            mViews = new ArrayList<View>();
            mViews.addAll(views);
        }

        @Override
        public void onAnimationStart(Animator animation) {
            for (View view : mViews) view.setVisibility(View.VISIBLE);
        }
    }

    public class IllegalParentViewException extends RuntimeException {

        public IllegalParentViewException(ViewParent illegalParent) {
            super(String.format("Illegal parent of LoadHideHelper view (parent is %s, should be FrameLayout)",
                    illegalParent.getClass().getSimpleName()));
        }

    }

}

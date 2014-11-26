package com.amlcurran.messages.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class AnimatorAssistant {

    private ValueAnimator fadeOutAnimator;

    public void startMessageSentAnimator(final BlockProgressBar blockProgressBar, final MessageSentAnimationEvents animationEvents) {
        fadeOutAnimator = ObjectAnimator.ofFloat(blockProgressBar.state, "progress", blockProgressBar.state.total);
        fadeOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                blockProgressBar.invalidate();
            }
        });
        fadeOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationEvents.finishedFilling();
                fadeOut(blockProgressBar, animationEvents);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fadeOutAnimator.setDuration(150);
        fadeOutAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeOutAnimator.start();
    }

    private void fadeOut(final View blockProgressBar, final MessageSentAnimationEvents animationEvents) {
        fadeOutAnimator = ObjectAnimator.ofFloat(blockProgressBar, "alpha", 0f);
        fadeOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationEvents.complete();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animationEvents.cancelled();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fadeOutAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeOutAnimator.setStartDelay(150);
        fadeOutAnimator.start();
    }

    public interface MessageSentAnimationEvents {
        void complete();
        void cancelled();
        void finishedFilling();
    }

}

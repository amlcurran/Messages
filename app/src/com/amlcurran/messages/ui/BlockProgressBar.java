package com.amlcurran.messages.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.amlcurran.messages.R;

public class BlockProgressBar extends View {

    private final State state = new State();
    private final Paint progressPaint;
    private ValueAnimator fadeOutAnimator;

    public BlockProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressPaint = new Paint();
        progressPaint.setColor(getResources().getColor(R.color.theme_colour));
    }

    public void setProgress(int progress) {
        //stopFadeOut();
        ValueAnimator animator = ObjectAnimator.ofFloat(state, "progress", progress);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        animator.setDuration(150);
        animator.setInterpolator(new DecelerateInterpolator(2));
        animator.start();
    }

    public void setProgressNoAnimation(int progress) {
        state.progress = progress;
        invalidate();
    }

    public void setTotal(int total) {
        //stopFadeOut();
        ValueAnimator animator = ObjectAnimator.ofFloat(state, "total", total);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        animator.setDuration(150);
        animator.setInterpolator(new DecelerateInterpolator(2));
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float completedFraction = state.progress / state.total;
        int completedPixels = (int) (completedFraction * getWidth());
        canvas.drawRect(0, 0, completedPixels, getHeight(), progressPaint);
    }

    public void animateMessageSent() {
        fadeOutAnimator = ObjectAnimator.ofFloat(state, "progress", state.total);
        fadeOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        fadeOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fadeOut();
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

    private void fadeOut() {
        fadeOutAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f);
        fadeOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setProgressNoAnimation(0);
                setAlpha(1f);
                fadeOutAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setProgressNoAnimation(0);
                setAlpha(1f);
                fadeOutAnimator = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fadeOutAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeOutAnimator.setStartDelay(150);
        fadeOutAnimator.start();
    }

    private class State {
        public float progress;
        public float total;

        public void setProgress(float progress) {
            this.progress = progress;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getProgress() {

            return progress;
        }

        public float getTotal() {
            return total;
        }
    }

    private class RenderState {

    }

}

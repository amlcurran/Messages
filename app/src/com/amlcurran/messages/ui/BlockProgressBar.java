package com.amlcurran.messages.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.amlcurran.messages.R;

public class BlockProgressBar extends View {

    private final State state = new State();
    private final Paint progressPaint;

    public BlockProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressPaint = new Paint();
        progressPaint.setColor(getResources().getColor(R.color.theme_colour));
    }

    public void setProgress(int progress) {
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

    public void setTotal(int total) {
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
        float completedFraction;
        if (isInEditMode()) {
            completedFraction = 0.6f;
        } else {
            completedFraction = state.progress / state.total;
        }
        int completedPixels = (int) (completedFraction * getWidth());
        canvas.drawRect(0, 0, completedPixels, getHeight(), progressPaint);
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

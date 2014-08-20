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

public class BlockProgressBar extends View implements AnimatorAssistant.MessageSentAnimationEvents {

    final State state = new State();
    private final Paint progressPaint;
    private final AnimatorAssistant animatorAssistant;

    public BlockProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressPaint = new Paint();
        progressPaint.setColor(getResources().getColor(R.color.theme_colour));
        animatorAssistant = new AnimatorAssistant();
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
        animatorAssistant.startMessageSentAnimator(this, this);
    }

    @Override
    public void complete() {
        animationEnded();
    }

    @Override
    public void cancelled() {
        animationEnded();
    }

    @Override
    public void finishedFilling() {

    }

    private void animationEnded() {
        setProgressNoAnimation(0);
        setAlpha(1f);
    }

    public void setTotalNoAnimation(int total) {
        state.total = total;
        invalidate();
    }

    class State {

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

}

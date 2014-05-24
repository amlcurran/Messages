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

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.amlcurran.messages.R;

public class LoadingView extends View implements TimeAnimator.TimeListener {

    public static final float ANIMATION_DURATION = 1500f;
    private final Paint linePaint;
    private final Drawable bubbleDrawable;
    private final float padding;
    private final TimeAnimator animator;
    private float duration;

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        bubbleDrawable = context.getResources().getDrawable(R.drawable.ic_sending_bg);
        padding = context.getResources().getDisplayMetrics().density * 4;
        animator = new TimeAnimator();
        animator.setTimeListener(this);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(bubbleDrawable.getIntrinsicWidth(), bubbleDrawable.getIntrinsicHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = bubbleDrawable.getIntrinsicWidth();
        int height = bubbleDrawable.getIntrinsicHeight();
        bubbleDrawable.setBounds(0, 0, width, height);
        bubbleDrawable.draw(canvas);
        for (int i = 0; i < 3; i++) {
            linePaint.setAlpha(getAlphaForLine(i));
            canvas.drawRect(2 * padding, (3 + 2 * i) * padding, 10 * padding, (4 + 2 * i) * padding, linePaint);
        }
    }

    private int getAlphaForLine(int line) {
        float elapsedFraction = 2*duration / ANIMATION_DURATION;

        return (int) (255 * elapsedFraction);
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        duration = Math.abs(ANIMATION_DURATION/2 - totalTime % ANIMATION_DURATION);
        invalidate();
    }
}

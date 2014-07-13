package com.amlcurran.messages.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

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
        this.state.progress = progress;
        invalidate();
    }

    public void setTotal(int total) {
        this.state.total = total;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float completedFraction;
        if (isInEditMode()) {
            completedFraction = 0.6f;
        } else {
            completedFraction = state.progress / (float) state.total;
        }
        int completedPixels = (int) (completedFraction * getWidth());
        canvas.drawRect(0, 0, completedPixels, getHeight(), progressPaint);
    }

    private class State {
        public int progress;
        public int total;
    }
}

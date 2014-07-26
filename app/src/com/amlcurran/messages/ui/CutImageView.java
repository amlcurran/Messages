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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.amlcurran.messages.R;

public class CutImageView extends ImageView {

    public static final double CUT_HEIGHT_OFFSET = 1.0;
    private final RectF circleRectF;
    private final RectF borderRectF;
    private final float borderWidth;
    protected final boolean drawOutline;
    private final BitmapClipCookieCutter bitmapClipCookieCutter;
    private Bitmap bitmapBuffer;

    public CutImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CutImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CutImageView);
        drawOutline = ta.getBoolean(R.styleable.CutImageView_drawOutline, true);

        borderWidth = context.getResources().getDimension(R.dimen.cut_image_border);

        circleRectF = new RectF();
        borderRectF = new RectF();
        Paint paint = new Paint();

        setImageResource(R.drawable.ic_contact_picture_unknown);

        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setShadowLayer(borderWidth, 0, 0, Color.GRAY);

        bitmapClipCookieCutter = new BitmapClipCookieCutter(this, paint, borderPaint, drawOutline);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxDimen = Math.max(getMeasuredHeight(), getMeasuredWidth());
        setMeasuredDimension(maxDimen, maxDimen);
        setCircleRect(circleRectF, maxDimen);
        insetRect(borderRectF, circleRectF, -borderWidth / 2);
        bitmapClipCookieCutter.updateCircleRect(circleRectF);
        bitmapClipCookieCutter.updateBorderRect(borderRectF);
        recycleBitmap();
        bitmapBuffer = Bitmap.createBitmap(maxDimen, maxDimen, Bitmap.Config.ARGB_8888);
    }

    protected void setCircleRect(RectF circleRectF, int maxDimen) {
        circleRectF.set(-2 * maxDimen, (float) (-CUT_HEIGHT_OFFSET * maxDimen),
                maxDimen - borderWidth, (float) (1 + CUT_HEIGHT_OFFSET) * maxDimen);
    }

    private void insetRect(RectF dest, RectF src, float offset) {
        dest.set(src.left - offset, src.top - offset, src.right + offset, src.bottom + offset);
    }

    private void recycleBitmap() {
        if (bitmapBuffer != null) {
            bitmapBuffer.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bitmapClipCookieCutter.cookieDraw(canvas);
    }

}

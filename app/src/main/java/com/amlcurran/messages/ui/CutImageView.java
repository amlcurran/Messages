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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CutImageView extends ImageView {

    public static final double CUT_HEIGHT_OFFSET = .8;
    private final RectF circleRectF;
    private final Paint paint;
    private Bitmap bitmapBuffer;

    public CutImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CutImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        circleRectF = new RectF();
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxDimen = Math.max(getMeasuredHeight(), getMeasuredWidth());
        setMeasuredDimension(maxDimen, maxDimen);
        circleRectF.set(-2 * maxDimen, (float) (-CUT_HEIGHT_OFFSET * maxDimen),
                maxDimen, (float) (1 + CUT_HEIGHT_OFFSET) * maxDimen);
        recycleBitmap();
        bitmapBuffer = Bitmap.createBitmap(maxDimen, maxDimen, Bitmap.Config.ARGB_8888);
    }

    private void recycleBitmap() {
        if (bitmapBuffer != null) {
            bitmapBuffer.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        if (b != null) {
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
            int w = getWidth(), h = getHeight();
            Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
            canvas.drawBitmap(roundBitmap, 0, 0, null);
        }
    }

    public Bitmap getCroppedBitmap(Bitmap source, int diameter) {
        Bitmap sbmp;
        if (source.getWidth() != diameter || source.getHeight() != diameter) {
            sbmp = Bitmap.createScaledBitmap(source, diameter, diameter, false);
        } else {
            sbmp = source;
        }
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawOval(circleRectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }

}

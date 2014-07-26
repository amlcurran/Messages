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

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

public class BitmapShaderCookieCutter implements CookieCutter {

    private final Paint paint;
    private final Paint borderPaint;
    private final boolean drawOutline;
    private final RectF borderRect;
    private final RectF circleRect;
    private final RectF scaledRect;
    private int viewHeight;
    private int viewWidth;
    private int bitmapHeight;
    private int bitmapWidth;

    public BitmapShaderCookieCutter(Paint borderPaint, boolean drawOutline) {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.borderPaint = borderPaint;
        this.drawOutline = drawOutline;
        this.circleRect = new RectF();
        this.borderRect = new RectF();
        this.scaledRect = new RectF();
    }

    @Override
    public void updateCircleRect(RectF circleRectF) {
        this.circleRect.set(circleRectF);
    }

    @Override
    public void updateBorderRect(RectF borderRectF) {
        this.borderRect.set(borderRectF);
    }

    @Override
    public void draw(Canvas canvas) {

        float widthScale = viewWidth / ((float) bitmapHeight);
        scaleRect(circleRect, widthScale);

        int count = canvas.save();
        canvas.scale(widthScale, widthScale);
        canvas.drawOval(circleRect, paint);
        canvas.restoreToCount(count);
        if (drawOutline) {
            canvas.drawArc(borderRect, 0, 360, true, borderPaint);
        }
    }

    private void scaleRect(RectF circleRect, float scale) {
        scaledRect.set(circleRect.left, circleRect.top,
                circleRect.width() * scale, circleRect.height() * scale);
    }

    @Override
    public void updateImage(Bitmap bitmap) {
        bitmapHeight = bitmap.getHeight();
        bitmapWidth = bitmap.getWidth();
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
    }

    @Override
    public void updateViewBounds(int height, int width) {
        viewHeight = height;
        viewWidth = width;
    }

    /*

        float bitmapProportion = viewHeight / ((float) this.bitmap.getHeight());
        int width = (int) (bitmapProportion * this.bitmap.getWidth());

        Bitmap scaledBitmap;
        if (viewHeight > 0) {
            scaledBitmap = Bitmap.createScaledBitmap(this.bitmap, width, viewHeight, true);
        } else {
            scaledBitmap = this.bitmap;
        }
     */
}

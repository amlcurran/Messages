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
    private int viewHeight;
    private int viewWidth;
    private Bitmap bitmap;

    public BitmapShaderCookieCutter(Paint paint, Paint borderPaint, boolean drawOutline) {
        this.paint = paint;
        this.borderPaint = borderPaint;
        this.drawOutline = drawOutline;
        this.circleRect = new RectF();
        this.borderRect = new RectF();
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
        canvas.drawOval(circleRect, paint);
        if (drawOutline) {
            canvas.drawArc(borderRect, 0, 360, true, borderPaint);
        }
    }

    @Override
    public void updateImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        setShader();
    }

    private void setShader() {
        float bitmapProportion = viewHeight / ((float) this.bitmap.getHeight());
        int width = (int) (bitmapProportion * this.bitmap.getWidth());

        Bitmap scaledBitmap;
        if (viewHeight > 0) {
            scaledBitmap = Bitmap.createScaledBitmap(this.bitmap, width, viewHeight, true);
        } else {
            scaledBitmap = this.bitmap;
        }
        BitmapShader shader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
    }

    @Override
    public void updateViewBounds(int height, int width) {
        this.viewHeight = height;
        this.viewWidth = width;
        if (this.bitmap != null) {
            setShader();
        }
    }
}

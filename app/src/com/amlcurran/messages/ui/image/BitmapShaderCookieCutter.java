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

package com.amlcurran.messages.ui.image;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class BitmapShaderCookieCutter implements CookieCutter {

    private final Paint photoPaint;
    private final Paint borderPaint;
    private final boolean drawOutline;
    private final RectF borderRect;
    private final RectF circleRect;
    private final Drawable selectorDrawable;
    private final Paint selectorPaint;
    private int viewHeight;
    private int viewWidth;
    private Bitmap bitmap;
    private boolean paintRequiresUpdate;

    public BitmapShaderCookieCutter(Paint borderPaint, boolean drawOutline, Drawable selectorDrawable) {
        this.selectorDrawable = selectorDrawable;
        this.photoPaint = new Paint();
        this.photoPaint.setAntiAlias(true);
        this.photoPaint.setColor(Color.WHITE);
        this.photoPaint.setAlpha(0);
        this.selectorPaint = new Paint();
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
        canvas.drawOval(circleRect, photoPaint);
        if (drawOutline) {
            canvas.drawArc(borderRect, 0, 360, true, borderPaint);
        }
    }

    @Override
    public void updateImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.paintRequiresUpdate = true;
    }

    @Override
    public void updateViewBounds(int height, int width) {
        viewHeight = height;
        viewWidth = width;
        selectorDrawable.setBounds(0, 0, width, height);
    }

    @Override
    public void preDraw() {
        if (paintRequiresUpdate && bitmap != null && viewWidth > 0 && viewHeight > 0) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, viewWidth, viewHeight, true);
            BitmapShader shader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            photoPaint.setAlpha(255);
            photoPaint.setShader(shader);

            Bitmap selectorBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_4444);
            selectorDrawable.draw(new Canvas(selectorBitmap));
            BitmapShader shader2 = new BitmapShader(selectorBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            selectorPaint.setShader(shader2);
            paintRequiresUpdate = false;
        }

    }

    @Override
    public void drawWithSelector(Canvas canvas) {
        draw(canvas);
        canvas.drawOval(circleRect, selectorPaint);
    }

}

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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class NewShaderCookieCutter implements CookieCutter {

    private final RectF destination = new RectF();
    private final RectF source = new RectF();
    private final RectF sourceSelector = new RectF();
    private final Matrix matrix = new Matrix();
    private final Matrix selectorMatrix = new Matrix();
    private final Paint selectorPaint;
    private final Paint photoPaint;
    private final Drawable selectorDrawable;
    private final Paint borderPaint;
    private final boolean drawOutline;
    private final RectF circleRect;
    private final RectF borderRect;
    private BitmapShader shader;
    private Bitmap bitmap;
    private final Shader selectorShader;

    public NewShaderCookieCutter(Paint borderPaint, boolean drawOutline, Drawable selectorDrawable) {
        this.selectorDrawable = selectorDrawable;
        this.photoPaint = new Paint();
        this.photoPaint.setAntiAlias(true);
        this.photoPaint.setDither(true);
        this.photoPaint.setFilterBitmap(true);
        this.photoPaint.setAlpha(0);
        this.selectorPaint = new Paint();
        this.borderPaint = borderPaint;
        this.drawOutline = drawOutline;
        this.circleRect = new RectF();
        this.borderRect = new RectF();
        this.selectorShader = initSelector(selectorDrawable);
    }

    private Shader initSelector(Drawable selectorDrawable) {
        Bitmap selectorBitmap = Bitmap.createBitmap(selectorDrawable.getIntrinsicWidth(),
                selectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(selectorBitmap);
        selectorDrawable.setBounds(0, 0, selectorDrawable.getIntrinsicWidth(), selectorDrawable.getIntrinsicHeight());
        selectorDrawable.draw(canvas);
        Shader selectorShader = new BitmapShader(selectorBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        selectorPaint.setShader(selectorShader);
        sourceSelector.set(0, 0, selectorDrawable.getIntrinsicWidth(), selectorDrawable.getIntrinsicHeight());
        return selectorShader;
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
        if (bitmap != null) {
            matrix.reset();
            matrix.setRectToRect(source, destination, Matrix.ScaleToFit.FILL);

            shader.setLocalMatrix(matrix);
            canvas.drawOval(circleRect, photoPaint);
            canvas.drawArc(borderRect, 0, 360, true, borderPaint);
        }
    }

    @Override
    public void updateImage(@NonNull Bitmap bitmap) {
        this.bitmap = bitmap;
        this.photoPaint.setAlpha(255);
        this.shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.photoPaint.setShader(shader);
        source.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    @Override
    public void updateViewBounds(int height, int width) {
        destination.set(0, 0, width, height);
    }

    @Override
    public void preDraw() {

    }

    @Override
    public void drawWithSelector(Canvas canvas) {
        draw(canvas);
        selectorMatrix.reset();
        selectorMatrix.setRectToRect(sourceSelector, destination, Matrix.ScaleToFit.FILL);
        selectorShader.setLocalMatrix(selectorMatrix);
        canvas.drawOval(circleRect, selectorPaint);
    }
}

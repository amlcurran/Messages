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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.amlcurran.messages.MessagesLog;

public class BitmapClipCookieCutter implements CookieCutter {

    private final CutImageView cutImageView;
    private final Paint borderPaint;
    private final Paint paint;
    private final boolean drawOutline;
    private RectF borderRectF;
    private RectF circleRectF;
    private Rect croppedRect;
    private Bitmap rawBitmap;

    public BitmapClipCookieCutter(CutImageView cutImageView, Paint paint, Paint borderPaint, boolean drawOutline) {
        this.cutImageView = cutImageView;
        this.paint = paint;
        this.drawOutline = drawOutline;
        this.borderPaint = borderPaint;
        this.croppedRect = new Rect();
    }

    @Override
    public void draw(Canvas canvas) {
        MessagesLog.d(hashCode(), hashCode() + " draw");
        Drawable drawable = cutImageView.getDrawable();

        if (drawable == null) {
            return;
        }

        if (cutImageView.getWidth() == 0 || cutImageView.getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        if (b != null) {
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
            int w = cutImageView.getWidth(), h = cutImageView.getHeight();
            Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
            canvas.drawBitmap(roundBitmap, 0, 0, null);
            if (drawOutline) {
                canvas.drawArc(borderRectF, 0, 360, true, borderPaint);
            }
        }
    }

    @Override
    public void updateImage(Bitmap bitmap) {
        Bitmap previousBitmapReference = rawBitmap;

        if (this.rawBitmap != null) {
            bitmap.recycle();
        }
    }

    @Override
    public void updateViewBounds(int height, int width) {

    }

    @Override
    public void preDraw() {

    }

    private Bitmap getCroppedBitmap(Bitmap source, int diameter) {
        Bitmap sbmp;
        if (source.getWidth() != diameter || source.getHeight() != diameter) {
            sbmp = Bitmap.createScaledBitmap(source, diameter, diameter, false);
        } else {
            sbmp = source;
        }
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        croppedRect.set(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.reset();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawOval(circleRectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, croppedRect, croppedRect, paint);


        return output;
    }

    @Override
    public void updateCircleRect(RectF circleRectF) {
        this.circleRectF = circleRectF;
    }

    @Override
    public void updateBorderRect(RectF borderRectF) {
        this.borderRectF = borderRectF;
    }
}
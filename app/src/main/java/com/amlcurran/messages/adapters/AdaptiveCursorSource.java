package com.amlcurran.messages.adapters;

import android.database.Cursor;

import com.espian.utils.Source;

public abstract class AdaptiveCursorSource<T> implements Source<T> {

    private Cursor cursor = CursorHelper.EMPTY;

    public void setCursor(Cursor cursor) {
        this.cursor.close();

        if (cursor == null) {
            this.cursor = CursorHelper.EMPTY;
        } else {
            this.cursor = cursor;
        }
    }

    @Override
    public T getAtPosition(int position) {
        cursor.moveToPosition(position);
        return getFromCursorRow(cursor);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    public abstract T getFromCursorRow(Cursor cursor);

}

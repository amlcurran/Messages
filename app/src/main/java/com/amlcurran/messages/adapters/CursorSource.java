package com.amlcurran.messages.adapters;

import android.database.Cursor;

public class CursorSource extends AdaptiveCursorSource<Cursor> {

    @Override
    public Cursor getFromCursorRow(Cursor cursor) {
        return cursor;
    }
}

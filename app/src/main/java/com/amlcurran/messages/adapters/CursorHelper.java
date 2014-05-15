package com.amlcurran.messages.adapters;

import android.database.Cursor;

public class CursorHelper {

    static String fromColumn(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

}

package com.amlcurran.messages.loaders;

import android.database.Cursor;

public interface CursorLoadListener {
    void onCursorLoaded(Cursor cursor);
}

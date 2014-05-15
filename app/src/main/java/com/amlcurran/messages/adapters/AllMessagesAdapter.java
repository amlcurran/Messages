package com.amlcurran.messages.adapters;

import android.content.Context;
import android.database.Cursor;

import com.espian.utils.Binder;
import com.espian.utils.Source;
import com.espian.utils.SourceBinderAdapter;

public class AllMessagesAdapter extends SourceBinderAdapter<Cursor> {

    public AllMessagesAdapter(Context context, Source<Cursor> source, Binder<Cursor> binder) {
        super(context, source, binder);
    }
}

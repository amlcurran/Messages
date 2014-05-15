package com.espian.utils;

import android.content.Context;
import android.view.View;

public abstract class SimpleBinder<T> implements Binder<T> {

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getSourcePosition(int position) {
        return position;
    }

    @Override
    public abstract View bindView(View convertView, T item, int position);

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public abstract View createView(Context context);
}

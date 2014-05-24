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

package com.espian.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SourceBinderAdapter<T> extends BaseAdapter {

    private final Binder<T> binder;
    private final Source<T> source;
    private final Context context;

    public SourceBinderAdapter(Context context, Source<T> source, Binder<T> binder) {
        this.context = context;
        this.source = source;
        this.binder = binder;
    }

    @Override
    public int getViewTypeCount() {
        return binder.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return binder.getItemViewType(position, getItem(position));
    }

    @Override
    public int getCount() {
        return source.getCount();
    }

    @Override
    public T getItem(int position) {
        int sourcePosition = binder.getSourcePosition(position);
        return sourcePosition == -1 ? null : source.getAtPosition(sourcePosition);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = binder.createView(context, getItemViewType(position));
        }
        return binder.bindView(convertView, getItem(position), position);
    }

}

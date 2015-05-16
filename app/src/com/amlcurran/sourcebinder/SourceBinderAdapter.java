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

package com.amlcurran.sourcebinder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.amlcurran.sourcebinder.source.Source;

import java.util.List;

public class SourceBinderAdapter<T> extends BaseAdapter implements Source.SourceChangeListener<T> {

    private final Binder<T> binder;
    private final Source<T> source;
    private final Context context;

    public SourceBinderAdapter(Context context, Source<T> source, Binder<T> binder) {
        this.context = context;
        this.source = source;
        this.binder = binder;
        this.source.setSourceChangeListener(this);
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
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = binder.createView(context, getItemViewType(position), parent);
        }
        return binder.bindView(convertView, getItem(position), position);
    }

    @Override
    public void sourceChanged(List items) {
        notifyDataSetChanged();
    }

    @Override
    @Deprecated
    /**
     * @deprecated notifying changed datasets should be the responsibility of the source (see {@link com.espian.utils.data.Source.SourceChangeListener}).
     */
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void itemAdded(int position, T item) {
        super.notifyDataSetChanged();
    }

    @Override
    public void itemRemoved(int removedIndex, T item) {
        super.notifyDataSetChanged();
    }

    @Override
    public void itemChanged(int index, T item) {
        super.notifyDataSetChanged();
    }
}

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

package com.amlcurran.sourcebinder.recyclerview;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.amlcurran.sourcebinder.source.Source;

import java.util.List;

public class RecyclerSourceBinderAdapter<Item, Holder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<Holder> {

    private final Source<Item> source;
    private final ViewHolderBinder<Item, Holder> viewHolderBinder;
    private final Handler handler;

    public RecyclerSourceBinderAdapter(Source<Item> source, ViewHolderBinder<Item, Holder> viewHolderBinder) {
        this.source = source;
        this.source.setSourceChangeListener(new UpdateSelfListener());
        this.viewHolderBinder = viewHolderBinder;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void setSourceChangeListener(Source.SourceChangeListener listener) {
        this.source.setSourceChangeListener(listener);
    }

    public void setDefaultSourceChangeListener() {
        setSourceChangeListener(new UpdateSelfListener());
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return viewHolderBinder.createViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        viewHolderBinder.bindViewHolder(viewHolder, source.getAtPosition(position));
    }

    @Override
    public int getItemCount() {
        return source.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return viewHolderBinder.getItemViewHolderType(position, source.getAtPosition(position));
    }

    private class UpdateSelfListener implements Source.SourceChangeListener {
        @Override
        public void sourceChanged(List items) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void itemAdded(final int position, Object item) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(position);
                }
            });
        }

        @Override
        public void itemRemoved(final int removedIndex, Object item) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRemoved(removedIndex);
                }
            });
        }

        @Override
        public void itemChanged(final int index, Object item) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(index);
                }
            });
        }
    }
}

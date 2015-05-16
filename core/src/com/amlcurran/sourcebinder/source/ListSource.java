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

package com.amlcurran.sourcebinder.source;

import java.util.ArrayList;
import java.util.List;

public class ListSource<T> implements Source<T> {

    private final ArrayList<T> list;
    private SourceChangeListener<T> changeListener = new NullSourceChangeListener<T>();

    public ListSource() {
        list = new ArrayList<T>();
    }

    @Override
    public T getAtPosition(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public void setSourceChangeListener(SourceChangeListener<T> changeListener) {
        if (changeListener == null) {
            this.changeListener = new NullSourceChangeListener<T>();
        } else {
            this.changeListener = changeListener;
        }
    }

    @Override
    public int indexOf(T item) {
        return list.indexOf(item);
    }

    public void replace(List<T> items) {
        list.clear();
        list.addAll(items);
        changeListener.sourceChanged(items);
    }

    public void addAtEnd(T item) {
        list.add(item);
        changeListener.itemAdded(list.size() - 1, item);
    }

    public void add(int position, T item) {
        list.add(position, item);
        changeListener.itemAdded(position, item);
    }

    public void removeItem(T item) {
        int removalIndex = list.indexOf(item);
        if (removalIndex > 0) {
            list.remove(item);
            changeListener.itemRemoved(removalIndex, item);
        }
    }

    public void replaceAt(int index, T item) {
        list.set(index, item);
        changeListener.itemChanged(index, item);
    }

    private static class NullSourceChangeListener<T> implements SourceChangeListener<T> {
        @Override
        public void sourceChanged(List<T> items) {

        }

        @Override
        public void itemAdded(int position, T item) {

        }

        @Override
        public void itemRemoved(int removedIndex, T item) {

        }

        @Override
        public void itemChanged(int index, T item) {

        }
    }
}

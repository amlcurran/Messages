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

import java.util.List;

public interface Source<T> {
    T getAtPosition(int position);

    int getCount();

    void setSourceChangeListener(SourceChangeListener<T> changeListener);

    int indexOf(T item);

    interface SourceChangeListener<T> {

        void sourceChanged(List<T> items);

        void itemAdded(int position, T item);

        void itemRemoved(int removedIndex, T item);

        void itemChanged(int index, T item);
    }
}

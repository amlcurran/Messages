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

package com.amlcurran.messages.conversationlist;

import java.util.ArrayList;
import java.util.List;

public class SelectionStateHolder<T> {

    private final List<T> checkedItems = new ArrayList<>();

    public boolean isChecked(T item) {
        return checkedItems.contains(item);
    }

    public void flipItem(T item) {
        if (checkedItems.contains(item)) {
            checkedItems.remove(item);
        } else {
            checkedItems.add(item);
        }
    }

    public boolean hasAnyChecked() {
        return checkedItems.size() > 0;
    }

    public T firstItem() {
        return itemAt(0);
    }

    public boolean hasOneChecked() {
        return checkedItems.size() == 1;
    }

    public int checkedItemCount() {
        return checkedItems.size();
    }

    public T itemAt(int position) {
        return checkedItems.get(position);
    }

    public List<T> allItems() {
        return checkedItems;
    }

    public void clear() {
        checkedItems.clear();
    }
}

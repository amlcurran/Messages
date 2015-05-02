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
    public int getItemViewType(int position, T item) {
        return 0;
    }

    @Override
    public abstract View createView(Context context, int itemViewType, ViewGroup parent);
}

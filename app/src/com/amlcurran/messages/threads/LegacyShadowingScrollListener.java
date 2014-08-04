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

package com.amlcurran.messages.threads;

import android.widget.AbsListView;

import com.amlcurran.messages.R;

class LegacyShadowingScrollListener implements AbsListView.OnScrollListener {

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount > 0) {
            setUpViewShadow(listView, totalItemCount);
        }
    }

    private void setUpViewShadow(AbsListView listView, int totalItemCount) {
        boolean lastVisibleIsLast = listView.getLastVisiblePosition() == totalItemCount - 1;
        boolean lastViewIsAtBottom = listView.getChildAt(listView.getChildCount() - 1).getBottom() == listView.getBottom();

        if (lastVisibleIsLast && lastViewIsAtBottom) {
            listView.setBackgroundResource(0);
        } else {
            listView.setBackgroundResource(R.drawable.compose_shadow_background);
        }
    }
}

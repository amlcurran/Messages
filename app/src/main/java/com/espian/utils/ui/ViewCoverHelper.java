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

package com.espian.utils.ui;

import android.view.View;

public class ViewCoverHelper implements View.OnClickListener {
    private final View mCoverView;

    public ViewCoverHelper(View coverView) {
        mCoverView = coverView;
        mCoverView.setOnClickListener(this);
    }

    /**
     * Allow the cover view to be hidden when the back button is pressed
     * @return whether the cover has consumed the back button event or not
     */
    public boolean onBackPressed() {
        if (AnimationFactory.viewIsShowing(mCoverView)) {
            AnimationFactory.fadeScaleOut(mCoverView);
            return true;
        }
        return false;
    }

    /**
     * Perform an action corresponding to the button which shows the cover view
     * @return whether an action was performed or not (always true - the cover view
     * was either shown or hidden)
     */
    public boolean onCoverViewItemSelected() {
        if (AnimationFactory.viewIsShowing(mCoverView)) {
            AnimationFactory.fadeScaleOut(mCoverView);
        } else {
            AnimationFactory.fadeScaleIn(mCoverView);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        AnimationFactory.fadeScaleOut(mCoverView);
    }
}
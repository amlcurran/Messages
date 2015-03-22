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

package com.amlcurran.messages.ui;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class AnimationDirector {

    private final List<Animation> animationList = new ArrayList<>();

    public static float bound(float v) {
        v = Math.min(v, 1f);
        return Math.max(v, 0f);
    }

    public void addAnimation(Animation animation) {
        animationList.add(animation);
    }

    public void drawAtProportion(Canvas canvas, float proportion) {
        for (Animation animation : animationList) {
            if (isInBounds(proportion, animation)) {
                float animProportion = (proportion - animation.startProportion()) / (animation.endProportion() - animation.startProportion());
                animation.draw(canvas, bound(animProportion));
            }
        }
    }

    private boolean isInBounds(float proportion, Animation animation) {
        return animation.startProportion() < proportion;
    }

    public interface Animation {
        float startProportion();

        float endProportion();

        void draw(Canvas canvas, float myProportion);

    }

}

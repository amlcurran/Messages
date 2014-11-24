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

package com.amlcurran.messages.conversationlist.adapter;

import android.content.Context;
import android.text.style.TextAppearanceSpan;

import com.amlcurran.messages.R;
import com.amlcurran.messages.bucket.Truss;

public class TextFormatter {

    private Context activity;

    public TextFormatter(Context activity) {
        this.activity = activity;
    }

    CharSequence constructSummary(String preamble, String text) {
        return new Truss().pushSpan(new TextAppearanceSpan(activity, R.style.Material_Body2))
                .append(preamble)
                .popSpan()
                .append(" — ")
                .append(text)
                .build();
    }
}
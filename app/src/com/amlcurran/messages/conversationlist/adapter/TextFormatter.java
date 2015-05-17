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
import android.graphics.Typeface;
import android.support.annotation.StyleRes;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;

import com.amlcurran.messages.R;
import com.amlcurran.messages.bucket.Truss;
import com.amlcurran.messages.core.conversationlist.Conversation;

public class TextFormatter {

    private Context activity;
    private TextAppearanceSpan lightText;
    private TextAppearanceSpan darkText;

    public TextFormatter(Context activity) {
        this.activity = activity;
        this.lightText = new TextAppearanceSpan(activity, R.style.Material_Body1);
        this.darkText = new TextAppearanceSpan(activity, R.style.Material_Body2);
    }

    CharSequence fromMeSummary(Conversation conversation) {
        return new Truss().pushSpan(darkText)
                .append(activity.getString(R.string.from_me_preamble))
                .popSpan()
                .append(" — ")
                .pushSpan(lightText)
                .append(conversation.getSummaryText())
                .popSpan()
                .build();
    }

    CharSequence unreadDark(CharSequence charSequence) {
        return new Truss().pushSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.material_text_standard_dark)))
                .append(unread(charSequence))
                .popSpan()
                .build();
    }

    CharSequence unread(CharSequence charSequence) {
        return new Truss().pushSpan(new StyleSpan(Typeface.BOLD))
                .append(charSequence)
                .popSpan()
                .build();
    }

    CharSequence draftSummary(String text) {
        return new Truss().pushSpan(new TextAppearanceSpan(activity, R.style.Material_Body2_Coloured))
                .append(activity.getString(R.string.draft_preamble))
                .popSpan()
                .append(" — ")
                .append(text)
                .build();
    }

    CharSequence fromOtherSummary(Conversation item) {
        return new Truss().pushSpan(lightText)
                .append(item.getSummaryText())
                .build();
    }

    public CharSequence name(CharSequence displayName) {
        return styled(displayName, R.style.ConversationTitle);
    }

    public CharSequence styled(CharSequence charSequence, @StyleRes int styleId) {
        return new Truss()
                .pushSpan(new TextAppearanceSpan(activity, styleId))
                .append(charSequence)
                .popSpan()
                .build();
    }

}
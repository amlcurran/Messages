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

package com.amlcurran.messages.notifications;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.conversationlist.Conversation;

import java.util.List;

public class StyledTextFactory {

    CharSequence buildSenderList(List<Conversation> conversations) {
        String result = "";
        for (Conversation conversation : conversations) {
            result += conversation.getContact().getDisplayName() + ", ";
        }
        return result.substring(0, result.length() - 2);
    }

    CharSequence buildListSummary(Context context, List<Conversation> conversations) {
        return context.getResources().getQuantityString(R.plurals.unread_message, conversations.size(), conversations.size());
    }

    CharSequence getInboxLine(Conversation conversation) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(conversation.getContact().getDisplayName());
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#cccccc"));
        builder.setSpan(colorSpan, 0, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(" ");
        builder.append(conversation.getSummaryText());
        return builder;
    }

    CharSequence buildTicker(Conversation conversation) {
        SpannableStringBuilder builder = new SpannableStringBuilder(conversation.getContact().getDisplayName() + ": ");
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(conversation.getSummaryText());
        return builder;
    }
}

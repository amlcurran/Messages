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

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.SmsMessage;
import com.github.amlcurran.sourcebinder.SimpleBinder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class ThreadBinder extends SimpleBinder<SmsMessage> {

    private static final int ITEM_ME = 0;
    private static final int ITEM_THEM = 1;
    private static final int ITEM_ME_SENDING = 2;
    private static final float ALPHA_SCALING_MILLIS = 1000 * 60 * 4 * 60f; // four hours
    private static final float ALPHA_MIN_VALUE = 0.6f;
    private final DateFormat formatter = new SimpleDateFormat("HH:mm dd-MMM-yy");
    private final Date date = new Date();
    private final ListView listView;

    public ThreadBinder(ListView listView) {
        this.listView = listView;
    }

    @Override
    public View bindView(View convertView, SmsMessage item, int position) {

        date.setTime(item.getTimestamp());

        TextView bodyText = getTextView(convertView, android.R.id.text1);
        bodyText.setText(item.getBody());
        //bodyText.setAlpha(getAlphaForTime(item.getTimestamp()));
        if (!item.isSending()) {
            getTextView(convertView, android.R.id.text2).setText(formatter.format(date));
        }

        Linkify.addLinks(bodyText, Linkify.ALL);

        return convertView;
    }

    private float getAlphaForTime(long timestamp) {

        long deltaMillis = System.currentTimeMillis() - timestamp;
        float alphaFraction = 1 - deltaMillis / ALPHA_SCALING_MILLIS;
        alphaFraction = Math.max(alphaFraction, ALPHA_MIN_VALUE);

        return alphaFraction;
    }

    @Override
    public View createView(Context context, int itemViewType, ViewGroup parent) {
        if (itemViewType == ITEM_ME) {
            return LayoutInflater.from(context).inflate(R.layout.item_thread_item_me, listView, false);
        } else if (itemViewType == ITEM_THEM) {
            return LayoutInflater.from(context).inflate(R.layout.item_thread_item_them, listView, false);
        } else if (itemViewType == ITEM_ME_SENDING) {
            return LayoutInflater.from(context).inflate(R.layout.item_thread_item_me_sending, listView, false);
        }
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position, SmsMessage item) {
        if (item.isFromMe()) {
            return item.isSending() ? ITEM_ME_SENDING : ITEM_ME;
        }
        return ITEM_THEM;
    }

    private TextView getTextView(View convertView, int text1) {
        return (TextView) convertView.findViewById(text1);
    }

}

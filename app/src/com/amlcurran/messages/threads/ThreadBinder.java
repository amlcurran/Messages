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
        if (!item.isSending()) {
            getTextView(convertView, android.R.id.text2).setText(formatter.format(date));
        }

        Linkify.addLinks(bodyText, Linkify.ALL);

        return convertView;
    }

    @Override
    public View createView(Context context, int itemViewType, ViewGroup parent) {
        // This is icky
        SmsMessage.Type type = SmsMessage.Type.values()[itemViewType];

        int layoutId = R.layout.item_thread_item_them;
        switch (type) {

            case INBOX:
                layoutId = R.layout.item_thread_item_them;
                break;

            case SENT:
                layoutId = R.layout.item_thread_item_me;
                break;

            case SENDING:
                layoutId = R.layout.item_thread_item_me_sending;
                break;

            case FAILED:
                layoutId = R.layout.item_thread_item_me_failed;
                break;
        }

        return LayoutInflater.from(context).inflate(layoutId, listView, false);
    }

    @Override
    public int getViewTypeCount() {
        return SmsMessage.Type.values().length;
    }

    @Override
    public int getItemViewType(int position, SmsMessage item) {
        return item.getType().ordinal();
    }

    private TextView getTextView(View convertView, int text1) {
        return (TextView) convertView.findViewById(text1);
    }

}

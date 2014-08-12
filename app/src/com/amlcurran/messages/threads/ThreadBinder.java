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
import android.content.res.Resources;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private Resources resources;
    private ResendCallback resendCallback;

    public ThreadBinder(ListView listView, Resources resources, ResendCallback resendCallback) {
        this.listView = listView;
        this.resources = resources;
        this.resendCallback = resendCallback;
    }

    @Override
    public View bindView(View convertView, SmsMessage item, int position) {

        TextView bodyText = getTextView(convertView, android.R.id.text1);
        bodyText.setText(item.getBody());

        manipulateView(convertView, item);

        Linkify.addLinks(bodyText, Linkify.ALL);

        return convertView;
    }

    private boolean isFailed(SmsMessage item) {
        return (item.getType() == SmsMessage.Type.DRAFT);
    }

    private void manipulateView(View view, final SmsMessage smsMessage) {
        switch (smsMessage.getType()) {

            case FAILED:
                manipulateFailedView(view, smsMessage);
                break;

            case INBOX:
            case SENT:
                addTimestampView(view, smsMessage);

        }
    }

    private void addTimestampView(View view, SmsMessage smsMessage) {
        date.setTime(smsMessage.getTimestamp());
        getTextView(view, android.R.id.text2).setText(formatter.format(date));
    }

    private void manipulateFailedView(View view, SmsMessage smsMessage) {
        ImageView imageView = (ImageView) view.findViewById(R.id.failed_to_send_image);
        imageView.setColorFilter(resources.getColor(R.color.theme_alt_color_2));
        imageView.setOnClickListener(new ResendClickListener(smsMessage));
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

    public interface ResendCallback {
        void resend(SmsMessage message);
    }

    private class ResendClickListener implements View.OnClickListener {
        private final SmsMessage smsMessage;

        public ResendClickListener(SmsMessage smsMessage) {
            this.smsMessage = smsMessage;
        }

        @Override
        public void onClick(View v) {
            resendCallback.resend(smsMessage);
        }
    }
}

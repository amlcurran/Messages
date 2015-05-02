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

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.analysis.SmsMessageAnalyser;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.sourcebinder.recyclerview.ViewHolderBinder;

class ThreadRecyclerBinder implements ViewHolderBinder<SmsMessage, ThreadRecyclerBinder.ViewHolder> {

    private final SmsMessageAnalyser smsMessageAnalyser;
    private Resources resources;
    private ResendCallback resendCallback;

    public ThreadRecyclerBinder(Resources resources, ResendCallback resendCallback) {
        this.resources = resources;
        this.resendCallback = resendCallback;
        this.smsMessageAnalyser = new SmsMessageAnalyser(new ResourcesDifferencesStringProvider(resources));
    }

    private void addTimestampView(ViewHolder viewHolder, SmsMessage smsMessage) {
        viewHolder.secondaryText.setText(smsMessageAnalyser.getDifferenceToNow(smsMessage.getTimestamp()));
    }

    private void showFailedIcon(ViewHolder viewHolder, SmsMessage smsMessage) {
        viewHolder.icon.setColorFilter(resources.getColor(R.color.theme_alt_color_2));
        viewHolder.icon.setOnClickListener(new ResendClickListener(smsMessage));
    }

    private int getResourceForMessageType(SmsMessage.Type type) {
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
        return layoutId;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup viewGroup, int i) {
        SmsMessage.Type type = SmsMessage.Type.values()[i];
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(getResourceForMessageType(type), viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindViewHolder(ViewHolder viewHolder, SmsMessage smsMessage) {
        viewHolder.bodyText.setText(smsMessage.getBody());
        Linkify.addLinks(viewHolder.bodyText, Linkify.ALL);

        if (smsMessage.getType() == SmsMessage.Type.FAILED) {
            showFailedIcon(viewHolder, smsMessage);
        } else if (smsMessage.getType() == SmsMessage.Type.INBOX || smsMessage.getType() == SmsMessage.Type.SENT) {
            addTimestampView(viewHolder, smsMessage);
        }
    }

    @Override
    public int getItemViewHolderType(int i, SmsMessage smsMessage) {
        return smsMessage.getType().ordinal();
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView bodyText;
        private final ImageView icon;
        private final TextView secondaryText;

        public ViewHolder(View view) {
            super(view);
            bodyText = ((TextView) view.findViewById(android.R.id.text1));
            icon = ((ImageView) view.findViewById(R.id.failed_to_send_image));
            secondaryText = ((TextView) view.findViewById(android.R.id.text2));
        }
    }
}

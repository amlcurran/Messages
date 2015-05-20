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

    private static final int ME = 0;
    private static final int THEM = 1;
    private final SmsMessageAnalyser smsMessageAnalyser;
    private final Resources resources;
    private final ResendCallback resendCallback;

    public ThreadRecyclerBinder(Resources resources, ResendCallback resendCallback) {
        this.resources = resources;
        this.resendCallback = resendCallback;
        this.smsMessageAnalyser = new SmsMessageAnalyser(new ResourcesDifferencesStringProvider(resources));
    }

    private int getResourceForMessageType(int type) {
        int layoutId = R.layout.item_thread_item_them;
        switch (type) {

            case THEM:
                layoutId = R.layout.item_thread_item_them;
                break;

            case ME:
                layoutId = R.layout.item_thread_item_me_sending;
                break;
        }
        return layoutId;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(getResourceForMessageType(i), viewGroup, false);
        return new ViewHolder(view, smsMessageAnalyser, resendCallback);
    }

    @Override
    public void bindViewHolder(ViewHolder viewHolder, SmsMessage smsMessage) {
        viewHolder.setBodyText(smsMessage);

        if (smsMessage.getType() == SmsMessage.Type.FAILED) {
            viewHolder.showFailedText();
            viewHolder.showFailedIcon(smsMessage, this);
            viewHolder.hideSendingIcon();
        } else if (smsMessage.getType() == SmsMessage.Type.INBOX) {
            viewHolder.addTimestampView(smsMessage);
        } else if (smsMessage.getType() == SmsMessage.Type.SENT) {
            viewHolder.addTimestampView(smsMessage);
            viewHolder.hideSendingIcon();
        } else if (smsMessage.getType() == SmsMessage.Type.SENDING) {
            viewHolder.showSendingIcon();
            viewHolder.hideFailedIcon();
        }
    }

    @Override
    public int getItemViewHolderType(int i, SmsMessage smsMessage) {
        return smsMessage.isFromMe() ? ME : THEM;
    }

    private static class ResendClickListener implements View.OnClickListener {
        private final SmsMessage smsMessage;
        private final ResendCallback resendCallback;

        public ResendClickListener(SmsMessage smsMessage, ResendCallback resendCallback) {
            this.smsMessage = smsMessage;
            this.resendCallback = resendCallback;
        }

        @Override
        public void onClick(View v) {
            resendCallback.resend(smsMessage);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView bodyText;
        private final ImageView icon;
        private final TextView secondaryText;
        private final View sendingImage;
        private final ResendCallback resendCallback;
        private SmsMessageAnalyser smsMessageAnalyser;

        public ViewHolder(View view, SmsMessageAnalyser smsMessageAnalyser, ResendCallback resendCallback) {
            super(view);
            this.resendCallback = resendCallback;
            this.bodyText = ((TextView) view.findViewById(android.R.id.text1));
            this.icon = ((ImageView) view.findViewById(R.id.failed_to_send_image));
            this.sendingImage = view.findViewById(R.id.sending_image);
            this.secondaryText = ((TextView) view.findViewById(android.R.id.text2));
            this.smsMessageAnalyser = smsMessageAnalyser;
        }

        public void hideSendingIcon() {
            animateOutView(sendingImage);
        }

        public void showSendingIcon() {
            if (sendingImage != null) {
                sendingImage.setVisibility(View.VISIBLE);
            }
        }

        public void hideFailedIcon() {
            animateOutView(icon);
        }

        private void animateOutView(final View icon) {
            if (icon != null) {
                icon.animate()
                        .alpha(0f)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                icon.setVisibility(View.GONE);
                            }
                        })
                        .start();
            }
        }

        private void showFailedIcon(SmsMessage smsMessage, ThreadRecyclerBinder threadRecyclerBinder) {
            icon.setVisibility(View.VISIBLE);
            icon.setColorFilter(threadRecyclerBinder.resources.getColor(R.color.theme_alt_color_2));
            icon.setOnClickListener(new ResendClickListener(smsMessage, resendCallback));
        }

        private void showFailedText() {
            secondaryText.setText(R.string.failed_to_send_touch_to_resend);
        }

        private void setBodyText(SmsMessage smsMessage) {
            bodyText.setText(smsMessage.getBody());
            Linkify.addLinks(bodyText, Linkify.ALL);
        }

        private void addTimestampView(SmsMessage smsMessage) {
            secondaryText.setText(smsMessageAnalyser.getDifferenceToNow(smsMessage.getTimestamp()));
        }
    }
}

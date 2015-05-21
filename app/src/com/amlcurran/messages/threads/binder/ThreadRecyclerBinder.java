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

package com.amlcurran.messages.threads.binder;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.analysis.SmsMessageAnalyser;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.threads.ResendCallback;
import com.amlcurran.messages.threads.ResourcesDifferencesStringProvider;
import com.amlcurran.sourcebinder.recyclerview.ViewHolderBinder;

public class ThreadRecyclerBinder implements ViewHolderBinder<SmsMessage, ViewHolder> {

    private static final int ME = 0;
    private static final int THEM = 1;

    private final SmsMessageAnalyser smsMessageAnalyser;
    private final ResendCallback resendCallback;

    public ThreadRecyclerBinder(Resources resources, ResendCallback resendCallback) {
        this.resendCallback = resendCallback;
        this.smsMessageAnalyser = new SmsMessageAnalyser(new ResourcesDifferencesStringProvider(resources));
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(getResourceForMessageType(i), viewGroup, false);
        return new ViewHolder(view, smsMessageAnalyser, resendCallback);
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
    public void bindViewHolder(ViewHolder viewHolder, SmsMessage smsMessage) {
        ViewModel viewModel = ViewModelFactory.get(smsMessage.getType());
        viewModel.render(viewHolder, smsMessage);
        viewHolder.setBodyText(smsMessage);
    }

    @Override
    public int getItemViewHolderType(int i, SmsMessage smsMessage) {
        return smsMessage.isFromMe() ? ME : THEM;
    }

}

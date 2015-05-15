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

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.telephony.SmsCounter;
import com.amlcurran.messages.threads.ComposeView;


public class ComposeMessageView extends LinearLayout implements View.OnClickListener, TextWatcher, ComposeView {

    private final EditText textEntryField;
    private final ImageButton sendButton;
    private final BlockProgressBar progress;
    private final SmsCounter smsCounter;
    private final TextView smsRequiredView;
    private ComposureCallbacks messageComposedListener = NONE;
    private int numberOfRequiredSms = 0;

    public ComposeMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComposeMessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.view_compose_message, this);
        smsRequiredView = ((TextView) findViewById(R.id.compose_sms_number));
        textEntryField = ((EditText) findViewById(R.id.thread_sms_entry));
        sendButton = ((ImageButton) findViewById(R.id.thread_sms_send));
        progress = (BlockProgressBar) findViewById(R.id.compose_progress);
        smsCounter = new SmsCounter();
        init();
    }

    private void init() {
        textEntryField.addTextChangedListener(this);
        sendButton.setOnClickListener(this);
        sendButton.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (sendButton.equals(v)) {
            sendMessage();
            clearEntry();
        }
    }

    private void clearEntry() {
        textEntryField.setText("", TextView.BufferType.EDITABLE);
        progress.animateMessageSent();
    }

    private void sendMessage() {
        messageComposedListener.onMessageComposed(textEntryField.getText());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        sendButton.setEnabled(!TextUtils.isEmpty(s));

        SmsCounter.SmsCount smsDeets = smsCounter.getSmsDeets(s);
        updateProgressBar(smsDeets);
        updateRequiredSmsView(smsDeets);
    }

    private void updateRequiredSmsView(SmsCounter.SmsCount smsDeets) {
        if (smsDeets.numberOfRequiredSms > 1) {
            smsRequiredView.setText(Integer.toString(smsDeets.numberOfRequiredSms));
            smsRequiredView.setVisibility(VISIBLE);
        } else {
            smsRequiredView.setVisibility(GONE);
        }
    }

    private void updateProgressBar(SmsCounter.SmsCount smsDeets) {
        progress.setTotalNoAnimation(smsDeets.messageLength);
        progress.setProgress(smsDeets.usedCharacters);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void setComposeListener(ComposureCallbacks messageComposedListener) {
        this.messageComposedListener = messageComposedListener;
    }

    private boolean hasText() {
        return !TextUtils.isEmpty(textEntryField.getText());
    }

    public void setText(String message) {
        textEntryField.setText(message);
    }

    public String getText() {
        return String.valueOf(textEntryField.getText());
    }

    @Override
    public String getComposedMessage() {
        return getText();
    }

    @Override
    public void setComposedMessage(String composedMessage) {
        setText(composedMessage);
    }

    @Override
    public void disable() {
        sendButton.setEnabled(false);
        textEntryField.setEnabled(false);
        textEntryField.setHint(R.string.hint_send_message_disabled);
    }

    @Override
    public void enable() {
        sendButton.setEnabled(hasText());
        textEntryField.setEnabled(true);
        textEntryField.setHint(R.string.hint_send_message);
    }

    public interface ComposureCallbacks {
        void onMessageComposed(CharSequence body);
    }

    public static final ComposureCallbacks NONE = new ComposureCallbacks() {
        @Override
        public void onMessageComposed(CharSequence body) {

        }
    };

}

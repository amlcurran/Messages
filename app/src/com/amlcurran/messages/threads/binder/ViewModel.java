package com.amlcurran.messages.threads.binder;

import com.amlcurran.messages.core.data.SmsMessage;

interface ViewModel {

    ViewModel ERROR = new ViewModel() {
        @Override
        public void render(ViewHolder viewHolder, SmsMessage smsMessage) {
            throw new RuntimeException("Attempted to get a view holder for invalid sms type");
        }
    };

    void render(ViewHolder viewHolder, SmsMessage smsMessage);
}

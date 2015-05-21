package com.amlcurran.messages.threads.binder;

import android.view.View;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.threads.ResendCallback;

import java.util.HashMap;
import java.util.Map;

class ViewModelFactory {
    private static final Map<SmsMessage.Type, ViewModel> modelMap = createModelMap();

    private static Map<SmsMessage.Type, ViewModel> createModelMap() {
        Map<SmsMessage.Type, ViewModel> modelMap = new HashMap<>();
        modelMap.put(SmsMessage.Type.FAILED, new FailedViewModel());
        modelMap.put(SmsMessage.Type.INBOX, new IncomingViewModel());
        modelMap.put(SmsMessage.Type.SENDING, new SendingViewModel());
        modelMap.put(SmsMessage.Type.SENT, new SentViewModel());
        return modelMap;
    }

    public static ViewModel get(SmsMessage.Type type) {
        ViewModel model = modelMap.get(type);
        if (model == null) {
            model = ViewModel.ERROR;
        }
        return model;
    }

    static class ResendClickListener implements View.OnClickListener {
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

    private static class FailedViewModel implements ViewModel {

        @Override
        public void render(ViewHolder viewHolder, SmsMessage smsMessage) {
            viewHolder.showFailedText();
            viewHolder.showFailedIcon(smsMessage);
            viewHolder.hideSendingIcon();
        }

    }

    private static class IncomingViewModel implements ViewModel {

        @Override
        public void render(ViewHolder viewHolder, SmsMessage smsMessage) {
            viewHolder.addTimestampView(smsMessage);
        }
    }

    private static class SentViewModel implements ViewModel {

        @Override
        public void render(ViewHolder viewHolder, SmsMessage smsMessage) {
            viewHolder.addTimestampView(smsMessage);
            viewHolder.hideSendingIcon();
        }
    }

    private static class SendingViewModel implements ViewModel {

        @Override
        public void render(ViewHolder viewHolder, SmsMessage smsMessage) {
            viewHolder.showSendingIcon();
            viewHolder.showSendingText();
            viewHolder.hideFailedIcon();
        }
    }
}

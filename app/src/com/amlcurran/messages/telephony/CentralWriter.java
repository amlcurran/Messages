package com.amlcurran.messages.telephony;

import android.content.Context;

import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.preferences.PreferenceStore;

public class CentralWriter {

    private final Context context;

    public CentralWriter(Context context) {
        this.context = context.getApplicationContext();
    }

    public void storeDraft(InFlightSmsMessage message) {
//        Intent intent = SmsAsyncService.getAsyncWriteIntent(getActivity(), message, WriteType.DRAFT);
//        getActivity().startService(intent);
        new PreferenceStore(context).storeDraft(message.getAddress(), message.getBody());
    }

    public void clearDraft(String sendAddress) {
        new PreferenceStore(context).clearDraft(sendAddress);
    }
}

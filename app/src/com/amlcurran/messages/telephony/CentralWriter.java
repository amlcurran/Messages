package com.amlcurran.messages.telephony;

import android.content.Context;

import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.preferences.PreferenceStoreDraftRepository;

public class CentralWriter {

    private final Context context;

    public CentralWriter(Context context) {
        this.context = context.getApplicationContext();
    }

    public void storeDraft(InFlightSmsMessage message) {
        new PreferenceStoreDraftRepository(context).storeDraft(message.getPhoneNumber(), message.getBody());
    }

    public void clearDraft(PhoneNumber phoneNumber) {
        new PreferenceStoreDraftRepository(context).clearDraft(phoneNumber);
    }
}

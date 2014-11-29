package com.amlcurran.messages.telephony;

import android.content.Context;

import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.preferences.PreferenceStoreDraftRepository;

public class SynchronousDatabaseWriter {

    private final Context context;

    public SynchronousDatabaseWriter(Context context) {
        this.context = context.getApplicationContext();
    }

    public void clearDraft(PhoneNumber phoneNumber) {
        new PreferenceStoreDraftRepository(context).clearDraft(phoneNumber);
    }
}

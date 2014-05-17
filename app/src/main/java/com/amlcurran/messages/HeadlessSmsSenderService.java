package com.amlcurran.messages;

import android.app.IntentService;
import android.content.Intent;

public class HeadlessSmsSenderService extends IntentService {

    public HeadlessSmsSenderService() {
        super("HeadlessSmsSenderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}

package com.amlcurran.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;

public class IncomingMessageReceiver extends BroadcastReceiver {

    private Context context;
    private Listener listener;

    public IncomingMessageReceiver(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void startListening() {
        context.registerReceiver(this, buildIntentFilter());
    }

    public void stopListening() {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onMessageReceived();
    }

    private IntentFilter buildIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        return filter;
    }

    public interface Listener {
        void onMessageReceived();
    }

}

package com.amlcurran.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class LocalMessageReceiver extends BroadcastReceiver {

    private Context context;
    private Listener listener;

    public LocalMessageReceiver(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void startListening() {
        LocalBroadcastManager.getInstance(context).registerReceiver(this, buildMessageFilter());
    }

    public void stopListening() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onMessageReceived();
    }

    private IntentFilter buildMessageFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SmsSender.BROADCAST_MESSAGE_SENT);
        filter.addAction(SmsReceiver.BROADCAST_MESSAGE_RECEIVED);
        return filter;
    }

    public interface Listener {
        void onMessageReceived();
    }

}

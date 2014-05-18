package com.amlcurran.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    public static final String TAG = SmsReceiver.class.getSimpleName();
    public static final String BROADCAST_MESSAGE_RECEIVED = "broadcast_message_received";

    private final SmsDatabaseWriter smsDatabaseWriter;

    public SmsReceiver() {
        smsDatabaseWriter = new SmsDatabaseWriter();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage message : messages) {
            writeSmsToProvider(context, message);
        }
    }

    private void sendLocalBroadcast(Context context) {
        Intent sentIntent = new Intent(BROADCAST_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(sentIntent);
    }

    private void writeSmsToProvider(Context context, SmsMessage message) {
        Log.d(TAG, "Writing SMS to provider: " + message.toString());

        boolean inserted = smsDatabaseWriter.writeInboxSms(context.getContentResolver(), message);
        if (inserted) {
            Log.d(TAG, "Sending broadcast of message received");
            sendLocalBroadcast(context);
            new Notifier(context).addNewMessageNotification(message);
        }

    }

}

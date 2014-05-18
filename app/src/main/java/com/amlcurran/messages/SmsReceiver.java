package com.amlcurran.messages;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    public static final String TAG = SmsReceiver.class.getSimpleName();
    public static final String BROADCAST_MESSAGE_RECEIVED = "broadcast_message_received";

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

        ContentValues contentValues = valuesFromMessage(message);

        Uri inserted = context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, contentValues);
        if (inserted != null) {
            Log.d(TAG, "Sending broadcast of message received");
            sendLocalBroadcast(context);
        }

        new Notifier(context).addNewMessageNotification(message);
    }

    private static ContentValues valuesFromMessage(SmsMessage message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Telephony.Sms.Inbox.BODY, message.getDisplayMessageBody());
        contentValues.put(Telephony.Sms.Inbox.ADDRESS, message.getDisplayOriginatingAddress());
        contentValues.put(Telephony.Sms.Inbox.DATE, message.getTimestampMillis());
        contentValues.put(Telephony.Sms.Inbox.DATE_SENT, message.getTimestampMillis());
        contentValues.put(Telephony.Sms.Inbox.TYPE, Telephony.Sms.Inbox.MESSAGE_TYPE_INBOX);
        return contentValues;
    }

}

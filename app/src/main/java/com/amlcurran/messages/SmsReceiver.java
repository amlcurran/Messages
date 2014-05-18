package com.amlcurran.messages;

import android.app.Notification;
import android.app.NotificationManager;
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

        ContentValues contentValues = new ContentValues();
        contentValues.put(Telephony.Sms.Inbox.BODY, message.getDisplayMessageBody());
        contentValues.put(Telephony.Sms.Inbox.ADDRESS, message.getDisplayOriginatingAddress());
        contentValues.put(Telephony.Sms.Inbox.DATE, message.getTimestampMillis());
        contentValues.put(Telephony.Sms.Inbox.DATE_SENT, message.getTimestampMillis());
        contentValues.put(Telephony.Sms.Inbox.TYPE, Telephony.Sms.Inbox.MESSAGE_TYPE_INBOX);

        Uri inserted = context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, contentValues);
        if (inserted != null) {
            Log.d(TAG, "Sending broadcast of message received");
            sendLocalBroadcast(context);
        }

        buildNotification(context, message.getDisplayOriginatingAddress(), message.getDisplayMessageBody(), message.getTimestampMillis());
    }

    private void buildNotification(Context context, String displayOriginatingAddress, String displayMessageBody, long timestampMillis) {
        Notification.BigTextStyle style = new Notification.BigTextStyle();
        style.bigText(displayMessageBody)
                .setBigContentTitle(displayOriginatingAddress);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(displayOriginatingAddress)
                .setContentText(displayMessageBody)
                .setSmallIcon(R.drawable.ic_notify_sms)
                .setStyle(style)
                .setWhen(timestampMillis)
                .build();
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(1, notification);
    }
}

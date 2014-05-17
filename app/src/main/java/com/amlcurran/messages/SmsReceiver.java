package com.amlcurran.messages;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage message : messages) {
            writeSmsToProvider(context, message);
        }
    }

    private void writeSmsToProvider(Context context, SmsMessage message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Telephony.Sms.Inbox.BODY, message.getDisplayMessageBody());
        contentValues.put(Telephony.Sms.Inbox.ADDRESS, message.getDisplayOriginatingAddress());
        contentValues.put(Telephony.Sms.Inbox.DATE, message.getTimestampMillis());
        contentValues.put(Telephony.Sms.Inbox.DATE_SENT, message.getTimestampMillis());
        contentValues.put(Telephony.Sms.Inbox.TYPE, Telephony.Sms.Inbox.MESSAGE_TYPE_INBOX);

        context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, contentValues);
        buildNotification(context, message.getDisplayOriginatingAddress(), message.getDisplayMessageBody(), message.getTimestampMillis());
    }

    private void buildNotification(Context context, String displayOriginatingAddress, String displayMessageBody, long timestampMillis) {
        Notification.BigTextStyle style = new Notification.BigTextStyle();
        style.bigText(displayMessageBody)
                .setBigContentTitle(displayOriginatingAddress)
                .setSummaryText(displayMessageBody);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(displayOriginatingAddress)
                .setSmallIcon(R.drawable.ic_notify_sms)
                .setStyle(style)
                .setWhen(timestampMillis)
                .build();
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(1, notification);
    }
}

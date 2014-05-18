package com.amlcurran.messages;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Notifier {

    private static final int ID_NEW_MESSAGE = 22;
    private final NotificationManager notificationManager;
    private final Context context;

    public Notifier(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void addNewMessageNotification(SmsMessage message) {
        Notification newMessageNotification = buildNotification(context, message.getAddress(),
                message.getBody(), message.getTimestamp());
        notificationManager.notify(ID_NEW_MESSAGE, newMessageNotification);
    }

    public void clearNewMessagesNotification() {
        notificationManager.cancel(ID_NEW_MESSAGE);
    }

    private Notification buildNotification(Context context, String displayOriginatingAddress, String displayMessageBody, long timestampMillis) {
        Notification.BigTextStyle style = new Notification.BigTextStyle()
                .bigText(displayMessageBody)
                .setBigContentTitle(displayOriginatingAddress);
        return getDefaultBuilder(context)
                .setContentTitle(displayOriginatingAddress)
                .setContentText(displayMessageBody)
                .setStyle(style)
                .setWhen(timestampMillis)
                .build();
    }

    private Notification.Builder getDefaultBuilder(Context context) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notify_sms);
    }
}

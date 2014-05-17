package com.amlcurran.messages;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class HeadlessSmsSenderService extends IntentService {

    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_SENT_DATE = "sent_date";
    public static final String ACTION_SEND_REQUEST = "send_request";
    public static final String ACTION_MESSAGE_SENT = "message_send";

    private final SmsManager smsManager;

    public HeadlessSmsSenderService() {
        super("HeadlessSmsSenderService");
        smsManager = SmsManager.getDefault();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isSendRequest(intent)) {
            sendMessage(intent);
        } else if (isSentNotification(intent)) {
            writeMessageToProvider(intent);
        }
    }

    private void writeMessageToProvider(Intent intent) {
        String address = intent.getStringExtra(EXTRA_ADDRESS);
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        long sentDate = intent.getLongExtra(EXTRA_SENT_DATE, 0);

        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.Sent.DATE, sentDate);
        values.put(Telephony.Sms.Sent.DATE_SENT, sentDate);
        values.put(Telephony.Sms.Sent.ADDRESS, address);
        values.put(Telephony.Sms.Sent.BODY, message);
        values.put(Telephony.Sms.Sent.TYPE, Telephony.Sms.Sent.MESSAGE_TYPE_SENT);

        Uri inserted = getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, values);
        Toast.makeText(this, String.valueOf(inserted), Toast.LENGTH_SHORT).show();
    }

    private void sendMessage(Intent intent) {
        String address = intent.getStringExtra(EXTRA_ADDRESS);
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        long sentDate = Calendar.getInstance().getTimeInMillis();
        ArrayList<PendingIntent> messageSendIntents = getMessageSendIntents(address, message, sentDate);
        smsManager.sendMultipartTextMessage(address, null, smsManager.divideMessage(message), messageSendIntents, null);
    }

    private boolean isSentNotification(Intent intent) {
        return intent.getAction().equals(ACTION_MESSAGE_SENT);
    }

    private boolean isSendRequest(Intent intent) {
        return intent.getAction().equals(ACTION_SEND_REQUEST);
    }

    public ArrayList<PendingIntent> getMessageSendIntents(String address, String message, long sentDate) {
        Intent intent = new Intent(ACTION_MESSAGE_SENT);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_SENT_DATE, sentDate);
        intent.setClass(this, HeadlessSmsSenderService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>();
        pendingIntents.add(pendingIntent);
        return pendingIntents;
    }
}

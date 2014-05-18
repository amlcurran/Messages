package com.amlcurran.messages;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class SmsSender extends IntentService {

    public static final String TAG = SmsSender.class.getSimpleName();

    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_SENT_DATE = "sent_date";
    public static final String ACTION_SEND_REQUEST = "send_request";
    public static final String ACTION_MESSAGE_SENT = "message_send";
    public static final String BROADCAST_MESSAGE_SENT = "broadcast_message_sent";

    private final SmsManager smsManager;
    private final SmsDatabaseWriter smsDatabaseWriter;

    public SmsSender() {
        super(TAG);
        smsManager = SmsManager.getDefault();
        smsDatabaseWriter = new SmsDatabaseWriter();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, intent.toString());
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
        Log.d(TAG, "Write sent message to provider " + message);

        boolean didWrite = smsDatabaseWriter.writeSentMessage(getContentResolver(), address, message, sentDate);
        if (didWrite) {
            sendLocalBroadcast();
        }
    }

    private void sendLocalBroadcast() {
        Intent sentIntent = new Intent(BROADCAST_MESSAGE_SENT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(sentIntent);
    }

    private void sendMessage(Intent intent) {
        String address = intent.getStringExtra(EXTRA_ADDRESS);
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        long sentDate = Calendar.getInstance().getTimeInMillis();

        Log.d(TAG, "Sending message: " + address);
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
        intent.setClass(this, SmsSender.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>();
        pendingIntents.add(pendingIntent);
        return pendingIntents;
    }
}

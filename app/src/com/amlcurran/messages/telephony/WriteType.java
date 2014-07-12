package com.amlcurran.messages.telephony;

import android.content.Intent;

public enum WriteType {
    INBOX, OUTBOX, DRAFT, SENT, FAILED;

    public static WriteType fromIntent(Intent intent) {
        return WriteType.valueOf(intent.getStringExtra(SmsReceiver.EXTRA_WRITE_TYPE));
    }
}

package com.amlcurran.messages;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;

public class SmsMessage implements Parcelable {

    private final String address;
    private final String body;
    private final long timestamp;

    public SmsMessage(String address, String body, long timestamp) {
        this.address = address;
        this.body = body;
        this.timestamp = timestamp;
    }

    private SmsMessage(Parcel in) {
        this.address = in.readString();
        this.body = in.readString();
        this.timestamp = in.readLong();
    }

    public static SmsMessage fromTelephoneApi(android.telephony.SmsMessage[] messages) {
        if (messages.length == 0) {
            throw new NullPointerException("Creating SMS message from empty array");
        }
        String address = messages[0].getDisplayOriginatingAddress();
        String body = createBody(messages);
        long timestamp = messages[0].getTimestampMillis();
        return new SmsMessage(address, body, timestamp);
    }

    private static String createBody(android.telephony.SmsMessage[] messages) {
        String result = "";
        for (android.telephony.SmsMessage message : messages) {
            result += message.getDisplayMessageBody();
        }
        return result;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("SMS with address: %1$s - timestamp: %2$d - body: %3$s", address, timestamp, body);
    }

    public static final Parcelable.Creator<SmsMessage> CREATOR = new Parcelable.Creator<SmsMessage>() {

        public SmsMessage createFromParcel(Parcel in) {
            return new SmsMessage(in);
        }

        public SmsMessage[] newArray(int size) {
            return new SmsMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(body);
        dest.writeLong(timestamp);
    }

    public ContentValues toContentValues(int messageTypeSent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Telephony.Sms.Inbox.BODY, body);
        contentValues.put(Telephony.Sms.Inbox.ADDRESS, address);
        contentValues.put(Telephony.Sms.Inbox.DATE, timestamp);
        contentValues.put(Telephony.Sms.Inbox.DATE_SENT, timestamp);
        contentValues.put(Telephony.Sms.Inbox.TYPE, messageTypeSent);
        return contentValues;
    }
}

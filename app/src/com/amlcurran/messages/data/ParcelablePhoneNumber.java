package com.amlcurran.messages.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.PhoneNumberUtils;

import com.amlcurran.messages.core.data.PhoneNumber;

public class ParcelablePhoneNumber implements Parcelable, PhoneNumber {

    private String formattedNumber;

    public ParcelablePhoneNumber(String number) {
        this.formattedNumber = PhoneNumberUtils.stripSeparators(number);
    }

    public ParcelablePhoneNumber(Parcel in) {
        // We can have confidence that the number is preformatted
        this.formattedNumber = in.readString();
    }

    @Override
    public String flatten() {
        return formattedNumber;
    }

    @Override
    public boolean isValid() {
        return PhoneNumberUtils.isWellFormedSmsAddress(formattedNumber);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PhoneNumber && formattedNumber.equals(((PhoneNumber) o).flatten());
    }

    @Override
    public int hashCode() {
        return formattedNumber.hashCode();
    }

    // Parcelable crud

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(formattedNumber);
    }

    public static final Creator<PhoneNumber> CREATOR = new Creator<PhoneNumber>() {

        public PhoneNumber createFromParcel(Parcel in) {
            return new ParcelablePhoneNumber(in);
        }

        public PhoneNumber[] newArray(int size) {
            return new PhoneNumber[size];
        }
    };

}

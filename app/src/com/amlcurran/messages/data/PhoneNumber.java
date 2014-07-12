package com.amlcurran.messages.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.PhoneNumberUtils;

public class PhoneNumber implements Parcelable {

    private String formattedNumber;

    public PhoneNumber(String number) {
        this.formattedNumber = PhoneNumberUtils.stripSeparators(number);
    }

    public PhoneNumber(Parcel in) {
        // We can have confidence that the number is preformatted
        this.formattedNumber = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PhoneNumber && formattedNumber.equals(((PhoneNumber) o).formattedNumber);
    }

    @Override
    public int hashCode() {
        return formattedNumber.hashCode();
    }

    @Override
    public String toString() {
        return formattedNumber;
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
            return new PhoneNumber(in);
        }

        public PhoneNumber[] newArray(int size) {
            return new PhoneNumber[size];
        }
    };

}

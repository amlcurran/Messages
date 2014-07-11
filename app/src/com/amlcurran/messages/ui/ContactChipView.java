package com.amlcurran.messages.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.amlcurran.messages.R;

public class ContactChipView extends ContactView {

    public ContactChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void inflate(LayoutInflater inflater) {
        inflater.inflate(R.layout.view_contact_chip, this, true);
    }
}

package com.amlcurran.messages.ui.contact;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;

public class ContactChipView extends DefaultContactView {

    private View removeButton;
    private RemoveListener removeListener = RemoveListener.NONE;

    public ContactChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void inflate(LayoutInflater inflater) {
        inflater.inflate(R.layout.view_contact_chip, this, true);
        removeButton = findViewById(R.id.chip_remove);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener.chipRemoveRequested(ContactChipView.this, contact);
            }
        });
    }

    public void showRemoveButton() {
        removeButton.setVisibility(VISIBLE);
    }

    public void hideRemoveButton() {
        removeButton.setVisibility(GONE);
    }

    public void setRemoveListener(RemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    public interface RemoveListener {

        RemoveListener NONE = new RemoveListener() {

            @Override
            public void chipRemoveRequested(ContactChipView contactChipView, Contact contact) {
                // No-op
            }

        };

        void chipRemoveRequested(ContactChipView contactChipView, Contact contact);
    }
}

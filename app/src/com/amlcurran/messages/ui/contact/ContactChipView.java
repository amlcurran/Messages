package com.amlcurran.messages.ui.contact;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.Task;

public class ContactChipView extends LinearLayout implements ContactView {

    private final TwoViewContactFormatter contactFormatter;
    private final ImageView contactImageView;
    private final View removeButton;
    private RemoveListener removeListener = RemoveListener.NONE;
    private Contact contact;
    private Task currentTask;

    public ContactChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_contact_chip, this, true);
        removeButton = findViewById(R.id.chip_remove);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener.chipRemoveRequested(ContactChipView.this, contact);
            }
        });
        contactImageView = (ImageView) findViewById(R.id.image);
        contactImageView.setAlpha(0f);
        TextView nameTextField = (TextView) findViewById(android.R.id.text1);
        TextView secondTextField = (TextView) findViewById(android.R.id.text2);
        contactFormatter = new TwoViewContactFormatter(nameTextField, secondTextField);
    }

    @Override
    public void setContact(final Contact contact, MessagesLoader loader) {
        cancelCurrentTask();
        this.contact = contact;
        contactFormatter.format(contact);
        currentTask = loader.loadPhoto(contact, new AlphaInSettingListener(contactImageView));
    }

    private void cancelCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
        }
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

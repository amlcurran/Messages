package com.amlcurran.messages.ui.contact;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.photos.PhotoLoader;

public class ContactChipView extends LinearLayout implements ContactView {

    private final TwoViewContactFormatter contactFormatter;
    private final RemoveRequestClickListener removeRequestClickListener;
    private final EndToEndPhotoManager photoLoaderManager;

    public ContactChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_contact_chip, this, true);

        View removeButton = findViewById(R.id.chip_remove);
        removeRequestClickListener = new RemoveRequestClickListener(this);
        removeButton.setOnClickListener(removeRequestClickListener);

        photoLoaderManager = new EndToEndPhotoManager(this);
        contactFormatter = new TwoViewContactFormatter(this);
    }

    @Override
    public void setContact(final Contact contact, PhotoLoader loader) {
        photoLoaderManager.stopLoadingPhoto();
        removeRequestClickListener.contact = contact;
        contactFormatter.format(contact);
        photoLoaderManager.loadContactPhoto(contact, loader);
    }

    @Override
    public void setClickToView(ContactClickListener callback) {
        throw new UnsupportedOperationException("You haven't implemented this yet...");
    }

    public void setRemoveListener(RemoveListener removeListener) {
        removeRequestClickListener.removeListener = removeListener;
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

    private static class RemoveRequestClickListener implements OnClickListener {

        private RemoveListener removeListener = RemoveListener.NONE;
        private Contact contact;
        private final ContactChipView contactChipView;

        public RemoveRequestClickListener(ContactChipView contactChipView) {
            this.contactChipView = contactChipView;
        }

        @Override
        public void onClick(View v) {
            removeListener.chipRemoveRequested(contactChipView, contact);
        }
    }
}

package com.amlcurran.messages.notifications;

import android.app.FragmentManager;

import com.amlcurran.messages.bucket.ArrayHelper;

public class BlockingInUiDialogNotifier implements BlockingInUiNotifier {

    private FragmentManager fragmentManager;

    public BlockingInUiDialogNotifier(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void show(Callbacks callbacks, String title, String message, Dialog.Button... buttons) {
        ArrayHelper<Dialog.Button> arrayHelper = new ArrayHelper<Dialog.Button>(buttons);

        Dialog.create(title, message, arrayHelper.atIndex(0), arrayHelper.atIndex(1))
                .setCallbacks(callbacks)
                .show(fragmentManager, title);
    }

}

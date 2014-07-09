package com.amlcurran.messages.notifications;

import android.app.FragmentManager;

public class BlockingInUiDialogNotifier implements BlockingInUiNotifier {
    private FragmentManager fragmentManager;

    public BlockingInUiDialogNotifier(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void show(Callbacks callbacks, String title, String message, Dialog.Button... buttons) {
        Dialog.create(title, message, buttons[0], buttons[1])
                .setCallbacks(callbacks)
                .show(fragmentManager, title);
    }

}

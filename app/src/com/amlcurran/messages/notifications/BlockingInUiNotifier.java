package com.amlcurran.messages.notifications;

public interface BlockingInUiNotifier {
    void show(Callbacks callbacks, String title, String message, Dialog.Button... buttons);

    interface Callbacks {
        void positive();

        void negative();
    }
}

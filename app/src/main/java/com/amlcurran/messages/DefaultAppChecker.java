package com.amlcurran.messages;

import android.app.Activity;
import android.content.Intent;
import android.provider.Telephony;
import android.view.View;

import com.amlcurran.messages.ui.UiController;

public class DefaultAppChecker implements View.OnClickListener {

    private Activity activity;
    private UiController uiController;

    public DefaultAppChecker(Activity activity, UiController uiController) {
        this.activity = activity;
        this.uiController = uiController;
        this.uiController.getDisabledBanner().setOnClickListener(this);
    }

    public void checkSmsApp() {
        if (isDefaultSmsApp()) {
            uiController.hideDisabledBanner();
        } else {
            uiController.showDisabledBanner();
        }
    }

    private boolean isDefaultSmsApp() {
        return Telephony.Sms.getDefaultSmsPackage(activity).equals(activity.getPackageName());
    }

    @Override
    public void onClick(View v) {
        startSelectionActivity();
    }

    private void startSelectionActivity() {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.getPackageName());
        activity.startActivityForResult(intent, MessagesActivity.REQUEST_CHANGE_SMS_APP);
    }
}

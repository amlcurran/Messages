package com.amlcurran.messages.ui;

import android.app.Activity;
import android.view.View;

import com.amlcurran.messages.MessagesActivity;
import com.amlcurran.messages.R;

public class SinglePaneViewController implements ViewController {

    private View disabledBanner;
    private View newMessageButton;

    public SinglePaneViewController(MessagesActivity messagesActivity) {

    }

    @Override
    public boolean backPressed() {
        return false;
    }

    @Override
    public void hideDisabledBanner() {
        disabledBanner.setVisibility(View.GONE);
    }

    @Override
    public void showDisabledBanner() {
        disabledBanner.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSecondary() {

    }

    @Override
    public void showSecondary() {

    }

    @Override
    public void setContentView(Activity activity) {
        activity.setContentView(R.layout.activity_messages);
        initView(activity);
    }

    private void initView(Activity activity) {
        disabledBanner = activity.findViewById(R.id.disabled_banner);
        newMessageButton = activity.findViewById(R.id.button_new_message);
    }

    @Override
    public void hideNewMessageButton() {
        newMessageButton.animate()
                .translationYBy(-newMessageButton.getMeasuredHeight())
                .setDuration(150)
                .start();
    }

    @Override
    public void showNewMessageButton() {
        newMessageButton.animate()
                .translationYBy(newMessageButton.getMeasuredHeight())
                .setDuration(150)
                .start();
    }

    @Override
    public void disableNewMessageButton() {
        newMessageButton.setVisibility(View.GONE);
    }

    @Override
    public void enableNewMessageButton() {
        newMessageButton.setVisibility(View.VISIBLE);
    }

    @Override
    public int getMasterFrameId() {
        return R.id.container;
    }

    @Override
    public int getSecondaryFrameId() {
        return R.id.container;
    }

    @Override
    public boolean shouldPlaceOnBackStack() {
        return true;
    }
}

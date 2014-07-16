package com.amlcurran.messages.ui;

import android.app.Activity;

import com.amlcurran.messages.R;

public class SinglePaneViewController extends BaseViewController {

    public SinglePaneViewController(Callback callback) {
        super(callback);
    }

    @Override
    public boolean backPressed() {
        return false;
    }

    @Override
    public void hideSecondary() {
        showNewMessageButton();
    }

    @Override
    public void showSecondary() {
        hideNewMessageButton();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_messages;
    }

    @Override
    protected void initView(Activity activity) {
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

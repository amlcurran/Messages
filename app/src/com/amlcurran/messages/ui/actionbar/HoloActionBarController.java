package com.amlcurran.messages.ui.actionbar;

import android.support.v7.app.ActionBar;
import android.view.View;

public class HoloActionBarController {

    private ActionBar actionBar;

    public HoloActionBarController(ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    public void removeHeader() {
        actionBar.setCustomView(null);
        hidePersonChip();
    }

    private void hidePersonChip() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE);
    }

    public void addHeader(View headerView) {
        actionBar.setCustomView(headerView);
        showPersonChip();
    }

    private void showPersonChip() {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    public void hideHeader() {
        hidePersonChip();
    }

    public void showHeader() {
        showPersonChip();
    }
}

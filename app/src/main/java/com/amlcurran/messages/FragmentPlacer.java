package com.amlcurran.messages;

import android.view.View;

public interface FragmentPlacer {
    void loadMessagesListFragment();

    void replaceFragment(ThreadFragment fragment);

    View getView();
}

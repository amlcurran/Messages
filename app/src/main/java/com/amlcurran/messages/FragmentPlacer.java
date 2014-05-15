package com.amlcurran.messages;

public interface FragmentPlacer {
    void loadMessagesListFragment();

    void replaceFragment(ThreadFragment fragment);

    int getLayoutResource();
}

package com.amlcurran.messages;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

public class SinglePaneFragmentPlacer implements FragmentPlacer {

    private Activity activity;

    public SinglePaneFragmentPlacer(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void loadMessagesListFragment() {
        activity.getFragmentManager().beginTransaction()
                .add(R.id.container, new MessagesListFragment())
                .commit();
    }

    @Override
    public void replaceFragment(ThreadFragment fragment) {
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public View getView() {
        return LayoutInflater.from(activity).inflate(R.layout.activity_messages, null);
    }
}
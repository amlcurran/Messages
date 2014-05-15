package com.amlcurran.messages;

import android.app.Activity;
import android.support.v4.widget.SlidingPaneLayout;

public class SlidingPaneFragmentPlacer implements FragmentPlacer {

    private final Activity activity;

    public SlidingPaneFragmentPlacer(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void loadMessagesListFragment() {
        activity.getFragmentManager().beginTransaction()
                .add(R.id.container, new MessagesListFragment())
                .commit();
        getSlider().openPane();
    }

    private SlidingPaneLayout getSlider() {
        return (SlidingPaneLayout) activity.findViewById(R.id.sliding_pane);
    }

    @Override
    public void replaceFragment(ThreadFragment fragment) {
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.secondary, fragment)
                .addToBackStack(null)
                .commit();
        getSlider().closePane();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_messages_sliding;
    }

}

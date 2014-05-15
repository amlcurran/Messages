package com.amlcurran.messages.ui;

import android.app.Activity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;

import com.amlcurran.messages.MessagesListFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.ThreadFragment;

public class SlidingPaneFragmentPlacer implements FragmentPlacer {

    private final Activity activity;
    private SlidingPaneLayout slider;

    public SlidingPaneFragmentPlacer(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void loadMessagesListFragment() {
        activity.getFragmentManager().beginTransaction()
                .add(R.id.container, new MessagesListFragment())
                .commit();
        slider.openPane();
    }

    @Override
    public void replaceFragment(ThreadFragment fragment) {
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.secondary, fragment)
                .addToBackStack(null)
                .commit();
        slider.closePane();
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.activity_messages_sliding, null);
        slider = (SlidingPaneLayout) view.findViewById(R.id.sliding_pane);
        slider.setParallaxDistance((int) activity.getResources().getDimension(R.dimen.slider_parallax));
        return view;
    }

}

package com.amlcurran.messages;

import android.app.Activity;
import android.os.Bundle;

import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;


public class MessagesActivity extends Activity implements MessagesLoaderProvider,
    MessagesListFragment.Listener {

    private final MessagesLoader messagesLoader = new MessagesLoader(this);
    private FragmentPlacer fragmentPlacer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentPlacer = new SlidingPaneFragmentPlacer(this);

        setContentView(fragmentPlacer.getLayoutResource());

        if (savedInstanceState == null) {
            fragmentPlacer.loadMessagesListFragment();
        }

    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return messagesLoader;
    }

    @Override
    public void onConversationSelected(String threadId) {
        ThreadFragment fragment = ThreadFragment.create(threadId);
        fragmentPlacer.replaceFragment(fragment);
    }

}

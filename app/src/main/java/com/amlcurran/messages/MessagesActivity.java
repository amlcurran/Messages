package com.amlcurran.messages;

import android.app.Activity;
import android.os.Bundle;

import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.ui.UiController;
import com.amlcurran.messages.ui.SlidingPaneUiController;


public class MessagesActivity extends Activity implements MessagesLoaderProvider,
    MessagesListFragment.Listener {

    private final MessagesLoader messagesLoader = new MessagesLoader(this);
    private UiController uiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiController = new SlidingPaneUiController(this);

        setContentView(uiController.getView());

        if (savedInstanceState == null) {
            uiController.loadMessagesListFragment();
            uiController.loadEmptyFragment();
        }

    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return messagesLoader;
    }

    @Override
    public void onBackPressed() {
        if (!uiController.backPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConversationSelected(String threadId) {
        ThreadFragment fragment = ThreadFragment.create(threadId);
        uiController.replaceFragment(fragment);
    }

}

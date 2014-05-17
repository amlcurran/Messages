package com.amlcurran.messages;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;

import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.ui.UiController;
import com.amlcurran.messages.ui.SlidingPaneUiController;

import java.util.concurrent.Executors;


public class MessagesActivity extends Activity implements MessagesLoaderProvider,
        ConversationListFragment.Listener {

    private final MessagesLoader messagesLoader = new MessagesLoader(this, Executors.newCachedThreadPool());
    private UiController uiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiController = new SlidingPaneUiController(this);

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .build());
        }

        setContentView(uiController.getView());

        if (savedInstanceState == null) {
            uiController.loadMessagesListFragment();
            uiController.loadEmptyFragment();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages, menu);
        return super.onCreateOptionsMenu(menu);
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

package com.amlcurran.messages;

import android.app.Activity;
import android.os.Bundle;

import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.loaders.StandardMessagesLoader;


public class MessagesActivity extends Activity implements MessagesLoaderProvider {

    private final MessagesLoader messagesLoader = new StandardMessagesLoader(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        if (savedInstanceState == null) {
            loadMessagesListFragment();
        }

    }

    private void loadMessagesListFragment() {
        getFragmentManager().beginTransaction()
                .add(R.id.container, new MessagesListFragment())
                .commit();
    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return messagesLoader;
    }
}

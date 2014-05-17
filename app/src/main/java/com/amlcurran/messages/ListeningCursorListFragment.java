package com.amlcurran.messages;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;

import com.amlcurran.messages.adapters.AdaptiveCursorSource;
import com.amlcurran.messages.loaders.CursorLoadListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.espian.utils.SourceBinderAdapter;

/**
 * Defines a fragment that uses cursors from the Telephony API and listens to receiving of new messages
 */
public abstract class ListeningCursorListFragment extends ListFragment implements CursorLoadListener, IncomingMessageReceiver.Listener {
    protected SourceBinderAdapter adapter;
    protected AdaptiveCursorSource source;
    protected MessagesLoader messageLoader;
    private IncomingMessageReceiver messageReceiver;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        messageLoader = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class).get(activity).getMessagesLoader();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messageReceiver = new IncomingMessageReceiver(getActivity(), this);
    }

    @Override
    public void onStart() {
        super.onStart();
        messageReceiver.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        messageReceiver.stopListening();
    }

    @Override
    public void onCursorLoaded(Cursor cursor) {
        source.setCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    @Override
    public abstract void onMessageReceived();
}

package com.amlcurran.messages;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;

import com.amlcurran.messages.adapters.AllMessagesAdapter;
import com.amlcurran.messages.adapters.AllMessagesBinder;
import com.amlcurran.messages.adapters.CursorSource;
import com.amlcurran.messages.loaders.CursorLoadListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.espian.utils.SourceBinderAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessagesListFragment extends ListFragment implements CursorLoadListener {

    private SourceBinderAdapter adapter;
    private CursorSource source;
    private AllMessagesBinder binder;
    private MessagesLoader loader;

    public MessagesListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        source = new CursorSource();
        binder = new AllMessagesBinder();
        adapter = createConversationListAdapter();
        setListAdapter(adapter);

        loader.loadConversationList(this);
    }

    private SourceBinderAdapter createConversationListAdapter() {
        return new AllMessagesAdapter(getActivity(), source, binder);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loader = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class)
                .get(activity).getMessagesLoader();
    }

    @Override
    public void onCursorLoaded(Cursor cursor) {
        source.setCursor(cursor);
        adapter.notifyDataSetChanged();
    }
}

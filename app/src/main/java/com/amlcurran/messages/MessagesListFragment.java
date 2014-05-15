package com.amlcurran.messages;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.SimpleCursorAdapter;

import com.amlcurran.messages.loaders.CursorLoadListener;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessagesListFragment extends ListFragment implements CursorLoadListener {

    private MessagesLoaderProvider loaderProvider;
    private SimpleCursorAdapter adapter;

    public MessagesListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = createConversationListAdapter();
        setListAdapter(adapter);

        loaderProvider.getMessagesLoader().loadConversationList(this);
    }

    private SimpleCursorAdapter createConversationListAdapter() {
        return new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null,
                new String[] { Telephony.Sms.Inbox.PERSON, }, new int[] { android.R.id.text1 }, 0);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loaderProvider = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class).get(activity);
    }

    @Override
    public void onCursorLoaded(Cursor cursor) {
        adapter.swapCursor(cursor);
    }
}

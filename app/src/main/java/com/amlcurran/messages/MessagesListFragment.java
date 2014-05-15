package com.amlcurran.messages;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.AdapterView;

import com.amlcurran.messages.adapters.AllMessagesAdapter;
import com.amlcurran.messages.adapters.AllMessagesBinder;
import com.amlcurran.messages.adapters.CursorHelper;
import com.amlcurran.messages.adapters.CursorSource;
import com.amlcurran.messages.loaders.CursorLoadListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.espian.utils.SourceBinderAdapter;

public class MessagesListFragment extends ListFragment implements CursorLoadListener, AdapterView.OnItemClickListener {

    private SourceBinderAdapter adapter;
    private CursorSource source;
    private AllMessagesBinder binder;
    private MessagesLoader loader;
    private Listener listener;

    public MessagesListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        source = new CursorSource();
        binder = new AllMessagesBinder();

        adapter = createConversationListAdapter();
        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);

        loader.loadConversationList(this);
    }

    private SourceBinderAdapter createConversationListAdapter() {
        return new AllMessagesAdapter(getActivity(), source, binder);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loader = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class).get(activity).getMessagesLoader();
        listener = new ProviderHelper<Listener>(Listener.class).get(activity);
    }

    @Override
    public void onCursorLoaded(Cursor cursor) {
        source.setCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        source.getCursor().moveToPosition(position);
        String threadId = CursorHelper.fromColumn(source.getCursor(), Telephony.Sms.THREAD_ID);
        listener.onConversationSelected(threadId);
    }

    public interface Listener {
        void onConversationSelected(String threadId);
    }

}

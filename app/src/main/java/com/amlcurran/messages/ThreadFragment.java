package com.amlcurran.messages;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amlcurran.messages.adapters.CursorHelper;
import com.amlcurran.messages.adapters.CursorSource;
import com.amlcurran.messages.loaders.CursorLoadListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.espian.utils.SimpleBinder;
import com.espian.utils.SourceBinderAdapter;

public class ThreadFragment extends ListFragment implements CursorLoadListener {

    private static final String THREAD_ID = "threadId";

    private MessagesLoader messageLoader;
    private SourceBinderAdapter adapter;
    private CursorSource source;

    public static ThreadFragment create(String threadId) {
        Bundle bundle = new Bundle();
        bundle.putString(ThreadFragment.THREAD_ID, threadId);

        ThreadFragment fragment = new ThreadFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        messageLoader = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class).get(activity).getMessagesLoader();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        source = new CursorSource();
        adapter = new SourceBinderAdapter<Cursor>(getActivity(), source, new ThreadBinder());
        setListAdapter(adapter);

        String threadId = getArguments().getString(THREAD_ID);
        messageLoader.loadThread(threadId, this);
    }

    @Override
    public void onCursorLoaded(Cursor cursor) {
        source.setCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    private class ThreadBinder extends SimpleBinder<Cursor> {

        @Override
        public View bindView(View convertView, Cursor item, int position) {
            ((TextView) convertView).setText(CursorHelper.fromColumn(item, Telephony.Sms.BODY));
            return convertView;
        }

        @Override
        public View createView(Context context) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
        }

    }
}

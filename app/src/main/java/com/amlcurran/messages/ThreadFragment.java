package com.amlcurran.messages;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amlcurran.messages.adapters.CursorSource;
import com.amlcurran.messages.loaders.CursorLoadListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.espian.utils.SimpleBinder;
import com.espian.utils.SourceBinderAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        private final DateFormat formatter = new SimpleDateFormat("HH:mm dd-MMM-yy");
        private final Date date = new Date();

        @Override
        public View bindView(View convertView, Cursor item, int position) {

            ThreadMessage message = ThreadMessage.fromCursor(item);
            date.setTime(message.getTimestamp());
            int backgroundColor;
            if (message.isFromMe()) {
                backgroundColor = getResources().getColor(android.R.color.transparent);
            } else {
                backgroundColor = getResources().getColor(R.color.theme_colour_20);
            }

            getTextView(convertView, android.R.id.text1).setText(message.getBody());
            getTextView(convertView, android.R.id.text2).setText(formatter.format(date));
            convertView.setBackgroundColor(backgroundColor);

            return convertView;
        }

        @Override
        public View createView(Context context) {
            return LayoutInflater.from(context).inflate(R.layout.item_thread_item, null);
        }

        private TextView getTextView(View convertView, int text1) {
            return (TextView) convertView.findViewById(text1);
        }

    }
}

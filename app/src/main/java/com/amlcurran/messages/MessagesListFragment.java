package com.amlcurran.messages;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amlcurran.messages.adapters.CursorHelper;
import com.amlcurran.messages.adapters.CursorSource;
import com.amlcurran.messages.loaders.CursorLoadListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.espian.utils.SimpleBinder;
import com.espian.utils.SourceBinderAdapter;

public class MessagesListFragment extends ListFragment implements CursorLoadListener, AdapterView.OnItemClickListener {

    private SourceBinderAdapter adapter;
    private CursorSource source;
    private ConversationsBinder binder;
    private MessagesLoader loader;
    private Listener listener;

    public MessagesListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        source = new CursorSource();
        binder = new ConversationsBinder();

        adapter = createConversationListAdapter();
        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);

        loader.loadConversationList(this);
    }

    private SourceBinderAdapter createConversationListAdapter() {
        return new SourceBinderAdapter<Cursor>(getActivity(), source, binder);
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

    public static class ConversationsBinder extends SimpleBinder<Cursor> {

        @Override
        public View bindView(View convertView, Cursor item, int position) {
            String person = CursorHelper.fromColumn(item, Telephony.Sms.PERSON);
            String body = CursorHelper.fromColumn(item, Telephony.Sms.BODY);
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(person);
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(body);

            if (isNotRead(item)) {
                setUnreadStyle(convertView);
            } else {
                setReadStyle(convertView);
            }

            return convertView;
        }

        private void setReadStyle(View convertView) {
            ((TextView) convertView.findViewById(android.R.id.text1)).setTypeface(null, 0);
            ((TextView) convertView.findViewById(android.R.id.text2)).setTypeface(null, 0);
        }

        private void setUnreadStyle(View convertView) {
            ((TextView) convertView.findViewById(android.R.id.text1)).setTypeface(null, Typeface.BOLD);
            ((TextView) convertView.findViewById(android.R.id.text2)).setTypeface(null, Typeface.ITALIC);
        }

        private boolean isNotRead(Cursor item) {
            String s = CursorHelper.fromColumn(item, Telephony.Sms.Inbox.READ);
            return s.toLowerCase().equals("0");
        }

        @Override
        public View createView(Context context) {
            return LayoutInflater.from(context).inflate(R.layout.item_message_preview, null);
        }
    }
}

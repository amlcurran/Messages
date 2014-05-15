package com.amlcurran.messages;

import android.app.Activity;
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
import com.espian.utils.SimpleBinder;
import com.espian.utils.SourceBinderAdapter;

public class MessagesListFragment extends ListeningCursorListFragment implements CursorLoadListener, AdapterView.OnItemClickListener {

    private Listener listener;

    public MessagesListFragment() { }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        source = new CursorSource();
        adapter = new SourceBinderAdapter<Cursor>(getActivity(), source, new ConversationsBinder());
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

        loadConversations();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = new ProviderHelper<Listener>(Listener.class).get(activity);
    }

    @Override
    public void onMessageReceived() {
        loadConversations();
    }

    private void loadConversations() {
        messageLoader.loadConversationList(this);
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

    public class ConversationsBinder extends SimpleBinder<Cursor> {

        @Override
        public View bindView(View convertView, Cursor item, int position) {
            String person = CursorHelper.fromColumn(item, Telephony.Sms.PERSON);
            String body = CursorHelper.fromColumn(item, Telephony.Sms.BODY);
            TextView textView1 = getTextView(convertView, android.R.id.text1);
            TextView textView2 = getTextView(convertView, android.R.id.text2);

            textView1.setText(person);
            textView2.setText(body);

            if (isNotRead(item)) {
                setUnreadStyle(convertView, textView1, textView2);
            } else {
                setReadStyle(convertView, textView1, textView2);
            }

            return convertView;
        }

        private void setReadStyle(View convertView, TextView textView1, TextView textView2) {
            textView1.setTypeface(null, 0);
            textView2.setTypeface(null, 0);
            textView1.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
        }

        private void setUnreadStyle(View convertView, TextView textView1, TextView textView2) {
            textView1.setTypeface(null, Typeface.BOLD);
            textView2.setTypeface(null, Typeface.BOLD);
            textView1.setTextColor(getResources().getColor(R.color.theme_colour));
        }

        private TextView getTextView(View convertView, int text1) {
            return (TextView) convertView.findViewById(text1);
        }

        private boolean isNotRead(Cursor item) {
            String s = CursorHelper.fromColumn(item, Telephony.Sms.Inbox.READ);
            return s.toLowerCase().equals("0");
        }

        @Override
        public View createView(Context context, int itemViewType) {
            return LayoutInflater.from(context).inflate(R.layout.item_message_preview, null);
        }
    }
}

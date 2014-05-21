package com.amlcurran.messages.conversationlist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.amlcurran.messages.ListeningCursorListFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.espian.utils.ProviderHelper;
import com.espian.utils.SimpleBinder;
import com.espian.utils.SourceBinderAdapter;

import java.util.List;

public class ConversationListFragment extends ListeningCursorListFragment<Conversation> implements ConversationListListener, AdapterView.OnItemClickListener {

    protected SourceBinderAdapter<Conversation> adapter;
    protected ListArraySource<Conversation> source;
    private Listener listener;

    public ConversationListFragment() { }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        source = new ListArraySource<Conversation>();
        adapter = new SourceBinderAdapter<Conversation>(getActivity(), source, new ConversationsBinder());
        setListAdapter(adapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = new ProviderHelper<Listener>(Listener.class).get(activity);
    }

    @Override
    public void loadData(MessagesLoader loader) {
        loader.loadConversationList(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.onConversationSelected(source.getAtPosition(position));
    }

    @Override
    public void onConversationListLoaded(List<Conversation> conversations) {
        source.addAll(conversations);
        adapter.notifyDataSetChanged();
    }

    public interface Listener {
        void onConversationSelected(Conversation conversation);
    }

    public class ConversationsBinder extends SimpleBinder<Conversation> {

        @Override
        public View bindView(View convertView, Conversation item, int position) {
            TextView textView1 = getTextView(convertView, android.R.id.text1);
            TextView textView2 = getTextView(convertView, android.R.id.text2);

            textView1.setText(item.getName());
            textView2.setText(item.getBody());

            if (item.isRead()) {
                setReadStyle(convertView, textView1, textView2);
            } else {
                setUnreadStyle(convertView, textView1, textView2);
            }

            return convertView;
        }

        private void setReadStyle(View convertView, TextView textView1, TextView textView2) {
            textView1.setTypeface(null, 0);
            textView2.setTypeface(null, 0);
            textView1.setTextColor(getResources().getColor(android.R.color.primary_text_light));
        }

        private void setUnreadStyle(View convertView, TextView textView1, TextView textView2) {
            textView1.setTypeface(null, Typeface.BOLD);
            textView2.setTypeface(null, Typeface.BOLD);
            textView1.setTextColor(getResources().getColor(R.color.theme_colour));
        }

        private TextView getTextView(View convertView, int text1) {
            return (TextView) convertView.findViewById(text1);
        }

        @Override
        public View createView(Context context, int itemViewType) {
            return LayoutInflater.from(context).inflate(R.layout.item_message_preview, getListView(), false);
        }
    }

}

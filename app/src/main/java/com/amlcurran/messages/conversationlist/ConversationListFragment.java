package com.amlcurran.messages.conversationlist;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amlcurran.messages.ListeningCursorListFragment;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.espian.utils.ProviderHelper;
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
        ConversationsBinder binder = new ConversationsBinder(getResources(), getMessageLoader());
        adapter = new SourceBinderAdapter<Conversation>(getActivity(), source, binder);
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

}

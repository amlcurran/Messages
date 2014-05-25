/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlcurran.messages.conversationlist;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.amlcurran.messages.ConversationModalMarshall;
import com.amlcurran.messages.ListeningCursorListFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.data.Conversation;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.espian.utils.ProviderHelper;
import com.espian.utils.SourceBinderAdapter;

import java.util.List;

public class ConversationListFragment extends ListeningCursorListFragment<Conversation> implements ConversationListListener, AdapterView.OnItemClickListener {

    protected SourceBinderAdapter<Conversation> adapter;
    protected ListArraySource<Conversation> source;
    private Listener listener;
    private ImageView emptyView;
    private ConversationModalMarshall.Callback modalCallback;

    public ConversationListFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        emptyView = (ImageView) view.findViewById(R.id.empty);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        source = new ListArraySource<Conversation>();
        ConversationsBinder binder = new ConversationsBinder(getResources(), getMessageLoader());
        adapter = new SourceBinderAdapter<Conversation>(getActivity(), source, binder);
        setListAdapter(adapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setOnItemClickListener(this);
        getListView().setMultiChoiceModeListener(new ConversationModalMarshall(source, modalCallback));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = new ProviderHelper<Listener>(Listener.class).get(activity);
        modalCallback = new ProviderHelper<ConversationModalMarshall.Callback>(ConversationModalMarshall.Callback.class).get(activity);
    }

    @Override
    public String[] getActions() {
        return new String[] { MessagesLoader.ACTION_LIST_CHANGED };
    }

    @Override
    public void loadData(MessagesLoader loader, boolean isRefresh) {
        if (!isRefresh) {
            showLoadingUi();
        }
        loader.loadConversationList(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.onConversationSelected(source.getAtPosition(position));
    }

    @Override
    public void onConversationListLoaded(final List<Conversation> conversations) {
        onUiThread(new Runnable() {
            @Override
            public void run() {
                source.replace(conversations);
                adapter.notifyDataSetChanged();
                hideLoadingUi();
            }
        });
    }

    private void showLoadingUi() {
        ((AnimationDrawable) emptyView.getDrawable()).start();
        getListView().setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hideLoadingUi() {
        getListView().setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    public interface Listener {
        void onConversationSelected(Conversation conversation);
    }

}

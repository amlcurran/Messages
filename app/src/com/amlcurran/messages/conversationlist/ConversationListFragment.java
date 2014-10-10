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
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.core.events.Broadcast;
import com.amlcurran.messages.core.events.EventSubscriber;
import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.events.BroadcastEventSubscriber;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.preferences.PreferenceStoreDraftRepository;
import com.amlcurran.messages.preferences.SharedPreferenceStore;
import com.amlcurran.messages.transition.TransitionManager;
import com.amlcurran.messages.ui.control.Master;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.ArrayListSource;
import com.github.amlcurran.sourcebinder.SourceBinderAdapter;

import java.util.List;

public class ConversationListFragment extends ListFragment implements ConversationListListener, AdapterView.OnItemClickListener, PreferenceListener.ChangeListener, BroadcastEventSubscriber.Listener, Master {

    protected SourceBinderAdapter<Conversation> adapter;
    protected ArrayListSource<Conversation> source;
    private View emptyView;
    private ConversationModalMarshall.Callback modalCallback;
    private PreferenceListener preferenceListener;
    private MessagesLoader messageLoader;
    private EventSubscriber messageReceiver;
    private TransitionManager transitionManager;

    public ConversationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        emptyView = view.findViewById(R.id.empty);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messageLoader = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class).get(getActivity()).getMessagesLoader();
        messageReceiver = new BroadcastEventSubscriber(getActivity(), this);

        preferenceListener = new PreferenceListener(getActivity(), this, "unread_priority");
        source = new ArrayListSource<Conversation>();
        TextFormatter textFormatter = new TextFormatter(getActivity());
        ConversationsBinder binder = new ConversationsBinder(textFormatter, getResources(), messageLoader, new PreferenceStoreDraftRepository(getActivity()));
        adapter = new SourceBinderAdapter<Conversation>(getActivity(), source, binder);
        setListAdapter(adapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setDivider(null);
        getListView().setOnItemClickListener(this);
        getListView().setMultiChoiceModeListener(new ConversationModalMarshall(source, modalCallback));
    }

    @Override
    public void onStart() {
        super.onStart();
        messageReceiver.startListening(getActions());
        preferenceListener.startListening();
        loadData(messageLoader, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        preferenceListener.stopListening();
        messageReceiver.stopListening();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        transitionManager = ((TransitionManager.Provider) activity).getTransitionManager();
        modalCallback = new ProviderHelper<ConversationModalMarshall.Callback>(ConversationModalMarshall.Callback.class).get(activity);
    }

    public Broadcast[] getActions() {
        return new Broadcast[]{new Broadcast(BroadcastEventBus.BROADCAST_LIST_LOADED, null)};
    }

    public void loadData(MessagesLoader loader, boolean isRefresh) {
        if (!isRefresh) {
            showLoadingUi();
        }
        loader.loadConversationList(this, getSort());
    }

    private Sort getSort() {
        return new SharedPreferenceStore(getActivity()).getConversationSort();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Conversation conversation = source.getAtPosition(position);
        transitionManager.to().thread(conversation.getContact(), conversation.getThreadId(), null);
    }

    @Override
    public void onConversationListLoaded(final List<Conversation> conversations) {
        //SingletonManager.getNotifier(getActivity()).updateUnreadNotification(false);
        source.replace(conversations);
        hideLoadingUi();
    }

    private void showLoadingUi() {
        getListView().setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hideLoadingUi() {
        if (getView() != null) {
            getListView().setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void preferenceChanged(String requestKey) {
        onMessageReceived();
    }

    @Override
    public void onMessageReceived() {
        loadData(messageLoader, true);
    }

}

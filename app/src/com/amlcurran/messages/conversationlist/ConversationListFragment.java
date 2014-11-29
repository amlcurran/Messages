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

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.conversationlist.adapter.ConversationsBinder;
import com.amlcurran.messages.conversationlist.adapter.TextFormatter;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.threads.DefaultContactClickListener;
import com.amlcurran.messages.ui.control.Master;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.ArrayListSource;
import com.github.amlcurran.sourcebinder.SourceBinderAdapter;

public class ConversationListFragment extends ListFragment implements ConversationListView, Master {

    private View loadingView;
    private View emptyView;
    private ConversationListViewController conversationController;
    private ConversationSelectedListener conversationSelectedListener;

    public ConversationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        loadingView = view.findViewById(R.id.loading);
        emptyView = view.findViewById(R.id.empty);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayListSource<Conversation> source = new ArrayListSource<Conversation>();
        ConversationModalMarshall.Callback modalCallback = (ConversationModalMarshall.Callback) getActivity();
        MessagesLoader messageLoader = new ProviderHelper<>(MessagesLoader.Provider.class).get(getActivity()).getMessagesLoader();
        DependencyRepository dependencyRepository = (DependencyRepository) getActivity();
        conversationController = new ConversationListViewController(this, source, dependencyRepository, SingletonManager.getConversationList(getActivity()));

        TextFormatter textFormatter = new TextFormatter(getActivity());
        ConversationsBinder binder = new ConversationsBinder(textFormatter, getResources(), messageLoader, dependencyRepository.getDraftRepository(), dependencyRepository.getPreferenceStore());
        SourceBinderAdapter adapter = new SourceBinderAdapter<>(getActivity(), source, binder);
        setListAdapter(adapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setDivider(null);
        getListView().setOnItemClickListener(new NotifyControllerClickListener());
        getListView().setMultiChoiceModeListener(new ConversationModalMarshall(source, modalCallback, new DefaultContactClickListener(dependencyRepository)));
    }

    @Override
    public void onStart() {
        super.onStart();
        conversationController.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversationController.stop();
    }

    @Override
    public void showLoadingUi() {
        getListView().setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setConversationSelectedListener(ConversationSelectedListener conversationSelectedListener) {
        this.conversationSelectedListener = conversationSelectedListener;
    }

    @Override
    public void showEmptyUi() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyUi() {
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoadingUi() {
        if (getView() != null) {
            getListView().setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }
    }

    private class NotifyControllerClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            conversationSelectedListener.selectedPosition(position);
        }
    }
}

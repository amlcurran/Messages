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

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.conversationlist.adapter.AdapterPhotoLoader;
import com.amlcurran.messages.conversationlist.adapter.ConversationViewFactory;
import com.amlcurran.messages.conversationlist.adapter.ConversationViewHolder;
import com.amlcurran.messages.conversationlist.adapter.ConversationsRecyclerBinder;
import com.amlcurran.messages.conversationlist.adapter.TextFormatter;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.threads.DefaultContactClickListener;
import com.amlcurran.messages.ui.control.Master;
import com.amlcurran.sourcebinder.source.ListSource;
import com.amlcurran.sourcebinder.recyclerview.RecyclerSourceBinderAdapter;

public class ConversationListFragment extends Fragment implements ConversationListView, Master, ConversationListView.ConversationSelectedListener {

    private View loadingView;
    private View emptyView;
    private ConversationListViewController conversationController;
    private ConversationSelectedListener conversationSelectedListener;
    private RecyclerView recyclerView;
    private RecyclerSourceBinderAdapter<Conversation, ConversationViewHolder> adapter;
    private final ListSource<Conversation> source = new ListSource<>();
    private SelectionStateHolder<Conversation> selectionStateHolder;
    private ActionMode actionMode;
    private ConversationModalMarshall listener;

    public ConversationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_recycler, container, false);
        loadingView = view.findViewById(R.id.loading);
        emptyView = view.findViewById(R.id.empty);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DeleteThreadViewCallback deleteThreadsViewCallback = (DeleteThreadViewCallback) getActivity();
        DependencyRepository dependencyRepository = (DependencyRepository) getActivity();
        conversationController = new ConversationListViewController(this, source, dependencyRepository, SingletonManager.getConversationList(getActivity()));

        selectionStateHolder = new SelectionStateHolder<>();
        listener = new ConversationModalMarshall(new DefaultContactClickListener(dependencyRepository), deleteThreadsViewCallback,
                SingletonManager.getStatReporter(getActivity()), SingletonManager.getMessagesLoader(getActivity()), selectionStateHolder);

        TextFormatter textFormatter = new TextFormatter(getActivity());
        AdapterPhotoLoader adapterPhotoLoader = new AdapterPhotoLoader(SingletonManager.getPhotoLoader(getActivity()), getResources());
        ConversationViewFactory conversationViewFactory = new ConversationViewFactory(dependencyRepository.getPreferenceStore(), textFormatter, dependencyRepository.getDraftRepository(),
                adapterPhotoLoader);
        ConversationsRecyclerBinder binder = new ConversationsRecyclerBinder(this, selectionStateHolder, conversationViewFactory);
        adapter = new RecyclerSourceBinderAdapter<>(source, binder);
        source.setSourceChangeListener(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(dependencyRepository.getPreferenceStore().showLargeUnreadPreviews());
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
        recyclerView.setVisibility(View.GONE);
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
    public void itemRemovedAt(int position) {
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void itemChangedAt(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    public void invalidateList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void itemMoved(int oldPosition, int newPosition) {
        adapter.notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void newList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void hideLoadingUi() {
        if (getView() != null) {
            recyclerView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void selectedPosition(int position) {
        conversationSelectedListener.selectedPosition(position);
    }

    @Override
    public void secondarySelected(int position) {
        Conversation item = source.getAtPosition(position);
        selectionStateHolder.flipItem(item);
        adapter.notifyItemChanged(position);
        updateActionMode();
    }

    private void updateActionMode() {
        if (selectionStateHolder.hasAnyChecked()) {
            if (actionMode == null) {
                actionMode = recyclerView.startActionMode(listener);
            }
            actionMode.invalidate();
        } else {
            actionMode.finish();
            actionMode = null;
        }
    }

}

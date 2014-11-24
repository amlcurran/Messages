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

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.preferences.PreferenceListener;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.transition.TransitionManager;
import com.github.amlcurran.sourcebinder.ArrayListSource;

import java.util.Collections;
import java.util.List;

class ConversationListViewController implements ConversationListListener, ConversationListView.ConversationSelectedListener, ConversationList.Callbacks {
    private final ConversationListView conversationListView;
    private final ConversationList conversationList;
    private final PreferenceStore preferenceStore;
    private final TransitionManager transitionManager;
    private final PreferenceListener preferenceListener;
    private final ArrayListSource<Conversation> source;

    public ConversationListViewController(ConversationListView conversationListView, PreferenceListener preferenceListener, ArrayListSource<Conversation> source, DependencyRepository dependencyRepository, ConversationList conversationList) {
        this.conversationListView = conversationListView;
        this.conversationList = conversationList;
        this.preferenceStore = dependencyRepository.getPreferenceStore();
        this.transitionManager = dependencyRepository.getTransitionManager();
        this.preferenceListener = preferenceListener;
        this.source = source;
    }

    public void start() {
        conversationList.addCallbacks(this);
        preferenceListener.startListening(new RefreshOnPreferenceChangeListener(this, conversationList));
        conversationListView.setConversationSelectedListener(this);
    }

    public void stop() {
        conversationList.removeCallbacks(this);
        preferenceListener.stopListening();
        conversationListView.setConversationSelectedListener(null);
    }

    @Override
    public void onConversationListLoaded(List<Conversation> conversations) {
        source.replace(conversations);
        conversationListView.hideLoadingUi();
    }

    @Override
    public void selectedPosition(int position) {
        Conversation conversation = source.getAtPosition(position);
        transitionManager.to().thread(conversation.getContact(), conversation.getThreadId(), null);
    }

    @Override
    public void listLoading() {
        conversationListView.showLoadingUi();
    }

    @Override
    public void listLoaded(List<Conversation> conversations) {
        Collections.sort(conversations, preferenceStore.getConversationSortComparator());
        source.replace(conversations);
        conversationListView.hideLoadingUi();
    }

    @Override
    public void listInvalidated(List<Conversation> invalidatedList) {

    }
}

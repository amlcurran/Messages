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
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.conversationlist.ConversationList;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.core.conversationlist.SortPositions;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.transition.TransitionManager;
import com.amlcurran.sourcebinder.source.ListSource;

import java.util.List;

class ConversationListViewController implements ConversationListView.ConversationSelectedListener, ConversationList.Callbacks {
    private final ConversationListView conversationListView;
    private final ConversationList conversationList;
    private final PreferenceStore preferenceStore;
    private final TransitionManager transitionManager;
    private final ListSource<Conversation> source;

    public ConversationListViewController(ConversationListView conversationListView, ListSource<Conversation> source, DependencyRepository dependencyRepository, ConversationList conversationList) {
        this.conversationListView = conversationListView;
        this.conversationList = conversationList;
        this.preferenceStore = dependencyRepository.getPreferenceStore();
        this.transitionManager = dependencyRepository.getTransitionManager();
        this.source = source;
    }

    public void start() {
        conversationList.addCallbacks(this);
        conversationListView.setConversationSelectedListener(this);
    }

    public void stop() {
        conversationList.removeCallbacks(this);
        conversationListView.setConversationSelectedListener(null);
    }

    @Override
    public void selectedPosition(int position) {
        Conversation conversation = source.getAtPosition(position);
        transitionManager.to().thread(conversation.getContact(), conversation.getThreadId(), null);
    }

    @Override
    public void secondarySelected(int position) {

    }

    @Override
    public void listLoading() {
        conversationListView.showLoadingUi();
    }

    @Override
    public void listLoaded(List<Conversation> conversations) {
        source.replace(conversations);
        conversationListView.newList();
        conversationListView.hideLoadingUi();
        if (conversations.size() == 0) {
            conversationListView.showEmptyUi();
        } else {
            conversationListView.hideEmptyUi();
        }
    }

    @Override
    public void listInvalidated(List<Conversation> invalidatedList) {

    }

    @Override
    public void conversationDeleted(Conversation deletedConversation, List<Conversation> conversationList) {
        int position = positionInSource(deletedConversation);
        listLoaded(conversationList);
        conversationListView.itemRemovedAt(position);
    }

    @Override
    public void conversationMarkedUnread(Conversation conversation, List<Conversation> conversationList, SortPositions sortPositions) {
        int position = positionInSource(conversation);
        if (preferenceStore.getConversationSort() == Sort.DEFAULT) {
            conversationListView.itemChangedAt(position);
        } else {
            source.replace(conversationList);
            conversationListView.itemMoved(sortPositions.oldPosition, sortPositions.newPosition);
            conversationListView.itemChangedAt(sortPositions.newPosition);
        }
    }

    private int positionInSource(Conversation deletedConversation) {
        int position = 0;
        int count = source.getCount();
        for (int i = 0; i < count; i++) {
            if (deletedConversation.getThreadId().equals(source.getAtPosition(i).getThreadId())) {
                position = i;
                break;
            }
        }
        return position;
    }
}

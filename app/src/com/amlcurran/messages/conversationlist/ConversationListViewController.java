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
import com.amlcurran.messages.core.events.Broadcast;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.core.events.EventSubscriber;
import com.amlcurran.messages.core.preferences.PreferenceListener;
import com.amlcurran.messages.transition.TransitionManager;
import com.github.amlcurran.sourcebinder.ArrayListSource;

import java.util.List;

class ConversationListViewController implements ConversationListListener, EventSubscriber.Listener, PreferenceListener.ChangeListener, ConversationListView.ConversationSelectedListener {
    private final ConversationListView conversationListView;
    private final ConversationList conversationList;
    private final TransitionManager transitionManager;
    private final PreferenceListener preferenceListener;
    private final ArrayListSource<Conversation> source;
    private final EventSubscriber messageReceiver;

    public ConversationListViewController(ConversationListView conversationListView, PreferenceListener preferenceListener, ArrayListSource<Conversation> source, EventSubscriber messageReceiver, DependencyRepository dependencyRepository, ConversationList conversationList) {
        this.conversationListView = conversationListView;
        this.conversationList = conversationList;
        this.transitionManager = dependencyRepository.getTransitionManager();
        this.preferenceListener = preferenceListener;
        this.source = source;
        this.messageReceiver = messageReceiver;
    }

    public void start() {
        messageReceiver.startListening(this, new Broadcast(EventBus.BROADCAST_LIST_LOADED, null));
        preferenceListener.startListening(this);
        conversationListView.setConversationSelectedListener(this);
        loadData();
    }

    public void stop() {
        preferenceListener.stopListening();
        messageReceiver.stopListening();
        conversationListView.setConversationSelectedListener(null);
    }

    private void loadData() {
        conversationListView.showLoadingUi();
        conversationList.reloadConversations(this);
    }

    @Override
    public void onConversationListLoaded(final List<Conversation> conversations) {
        source.replace(conversations);
        conversationListView.hideLoadingUi();
    }

    @Override
    public void preferenceChanged(String requestKey) {
        conversationList.reloadConversations(this);
    }

    @Override
    public void onMessageReceived() {
        conversationList.reloadConversations(this);
    }

    @Override
    public void selectedPosition(int position) {
        Conversation conversation = source.getAtPosition(position);
        transitionManager.to().thread(conversation.getContact(), conversation.getThreadId(), null);
    }
}

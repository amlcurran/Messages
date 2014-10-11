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

import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.core.events.Broadcast;
import com.amlcurran.messages.core.events.EventSubscriber;
import com.amlcurran.messages.core.preferences.PreferenceListener;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.events.EventBus;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.transition.TransitionManager;
import com.github.amlcurran.sourcebinder.ArrayListSource;

import java.util.List;

class ConversationListViewController implements ConversationListListener, EventSubscriber.Listener, PreferenceListener.ChangeListener, ConversationListView.ConversationSelectedListener {
    private final ConversationListView conversationListView;
    private final MessagesLoader messageLoader;
    private final TransitionManager transitionManager;
    private final PreferenceListener preferenceListener;
    private final ArrayListSource<Conversation> source;
    private final EventSubscriber messageReceiver;
    private final PreferenceStore preferenceStore;

    public ConversationListViewController(ConversationListView conversationListView, MessagesLoader messageLoader, TransitionManager transitionManager, PreferenceListener preferenceListener, ArrayListSource<Conversation> source, EventSubscriber messageReceiver, PreferenceStore preferenceStore) {
        this.conversationListView = conversationListView;
        this.messageLoader = messageLoader;
        this.transitionManager = transitionManager;
        this.preferenceListener = preferenceListener;
        this.source = source;
        this.messageReceiver = messageReceiver;
        this.preferenceStore = preferenceStore;
    }

    public void start() {
        messageReceiver.startListening(this, getActions());
        preferenceListener.startListening(this);
        conversationListView.setConversationSelectedListener(this);
        loadData(messageLoader, false);
    }

    public void stop() {
        preferenceListener.stopListening();
        messageReceiver.stopListening();
        conversationListView.setConversationSelectedListener(null);
    }

    public Broadcast[] getActions() {
        return new Broadcast[]{new Broadcast(EventBus.BROADCAST_LIST_LOADED, null)};
    }

    public void loadData(MessagesLoader loader, boolean isRefresh) {
        if (!isRefresh) {
            conversationListView.showLoadingUi();
        }
        loader.loadConversationList(this, getSort());
    }

    private Sort getSort() {
        return preferenceStore.getConversationSort();
    }

    @Override
    public void onConversationListLoaded(final List<Conversation> conversations) {
        //SingletonManager.getNotifier(activity).updateUnreadNotification(false);
        source.replace(conversations);
        conversationListView.hideLoadingUi();
    }

    @Override
    public void preferenceChanged(String requestKey) {
        onMessageReceived();
    }

    @Override
    public void onMessageReceived() {
        loadData(messageLoader, true);
    }

    @Override
    public void selectedPosition(int position) {
        Conversation conversation = source.getAtPosition(position);
        transitionManager.to().thread(conversation.getContact(), conversation.getThreadId(), null);
    }
}

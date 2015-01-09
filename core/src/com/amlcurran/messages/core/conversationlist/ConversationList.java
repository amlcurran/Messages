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

package com.amlcurran.messages.core.conversationlist;

import com.amlcurran.messages.core.CommandQueue;
import com.amlcurran.messages.core.preferences.PreferenceStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConversationList {

    private final ConversationLoader conversationLoader;
    private final PreferenceStore preferenceStore;
    private final CommandQueue uiCommandQueue;
    private final List<Callbacks> callbacksList = new ArrayList<Callbacks>();
    private final List<Conversation> conversationList = new ArrayList<Conversation>();
    private LoadingState state;

    public ConversationList(ConversationLoader conversationLoader, PreferenceStore preferenceStore, CommandQueue uiCommandQueue) {
        this.conversationLoader = conversationLoader;
        this.preferenceStore = preferenceStore;
        this.uiCommandQueue = uiCommandQueue;
        this.state = LoadingState.INITIAL_LOAD;
        this.preferenceStore.listenToPreferenceChanges(new ResortListener());
    }

    public void addCallbacks(Callbacks callbacks) {
        loadIfFirstAttach();
        callbacksList.add(callbacks);
        updateCallback(callbacks);
    }

    private void loadIfFirstAttach() {
        if (state == LoadingState.INITIAL_LOAD) {
            reloadConversations();
        }
    }

    public void removeCallbacks(Callbacks callbacks) {
        callbacksList.remove(callbacks);
    }

    private void updateCallback(Callbacks callbacks) {
        switch (state) {

            case INITIAL_LOAD:
                postLoading(callbacks);
                break;

            case LOADED:
                postLoaded(callbacks, conversationList);
                break;

            case INVALIDATED:
                postInvalidated(callbacks, conversationList);
                break;

        }
    }

    private void postInvalidated(final Callbacks callbacks, final List<Conversation> conversationList) {
        uiCommandQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                callbacks.listInvalidated(conversationList);
            }
        });
    }

    private void postLoading(final Callbacks callbacks) {
        uiCommandQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                callbacks.listLoading();
            }
        });
    }

    private void postLoaded(final Callbacks callbacks, final List<Conversation> conversationList) {
        uiCommandQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                callbacks.listLoaded(conversationList);
            }
        });
    }

    private void postConversationDeleted(final Callbacks callbacks, final Conversation deletedConversation, final List<Conversation> conversationList) {
        uiCommandQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                callbacks.conversationDeleted(deletedConversation, conversationList);
            }
        });
    }

    public void reloadConversations() {
        state = LoadingState.INVALIDATED;
        for (Callbacks callbacks : callbacksList) {
            updateCallback(callbacks);
        }
        conversationLoader.loadConversationList(new ConversationListListener() {
            @Override
            public void onConversationListLoaded(List<Conversation> conversations) {
                state = LoadingState.LOADED;
                updateInternalList(conversations);
                for (Callbacks callbacks : callbacksList) {
                    postLoaded(callbacks, conversationList);
                }
            }
        });
    }

    private void updateInternalList(List<Conversation> conversations) {
        conversationList.clear();
        conversationList.addAll(conversations);
        sort();
    }

    private void sort() {
        Collections.sort(conversationList, preferenceStore.getConversationSortComparator());
    }

    public void deletedConversations(List<Conversation> deletedConversations) {
        for (Conversation deletedConversation : deletedConversations) {
            conversationList.remove(deletedConversation);
            for (Callbacks callbacks : callbacksList) {
                postConversationDeleted(callbacks, deletedConversation, conversationList);
            }
        }
    }

    public void markedUnread(String threadId) {
        Conversation conversation = findConversationById(threadId);
        conversation.isRead = false;
        int oldPosition = conversationList.indexOf(conversation);
        sort();
        int newPosition = conversationList.indexOf(conversation);
        for (Callbacks callbacks : callbacksList) {
            postConversationMarkedUnread(callbacks, conversation, conversationList, new SortPositions(oldPosition, newPosition));
        }
    }

    private Conversation findConversationById(String threadId) {
        for (Conversation item : conversationList) {
            if (threadId.equals(item.getThreadId())) {
                return item;
            }
        }
        return null;
    }

    private void postConversationMarkedUnread(final Callbacks callbacks, final Conversation conversation, final List<Conversation> conversationList, final SortPositions sortPositions) {
        uiCommandQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                callbacks.conversationMarkedUnread(conversation, conversationList, sortPositions);
            }
        });
    }

    public interface Callbacks {
        void listLoading();

        void listLoaded(List<Conversation> conversations);

        void listInvalidated(List<Conversation> invalidatedList);

        void conversationDeleted(Conversation deletedConversation, List<Conversation> conversationList);

        void conversationMarkedUnread(Conversation conversation, List<Conversation> conversationList, SortPositions sortPositions);
    }

    private enum LoadingState {
        INITIAL_LOAD, LOADED, INVALIDATED
    }

    private class ResortListener implements PreferenceStore.PreferenceChangedListener {
        @Override
        public void preferenceChanged(String key) {
            for (Callbacks callbacks : callbacksList) {
                sort();
                postLoaded(callbacks, conversationList);
            }
        }
    }
}

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

import android.os.Handler;

import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.loaders.MessagesLoader;

import java.util.ArrayList;
import java.util.List;

public class ConversationList {

    private final MessagesLoader messagesLoader;
    private final PreferenceStore preferenceStore;
    private final Handler uiHandler;
    private final List<Callbacks> callbacksList = new ArrayList<Callbacks>();
    private final List<Conversation> conversationList = new ArrayList<Conversation>();
    private LoadingState state;

    public ConversationList(MessagesLoader messagesLoader, PreferenceStore preferenceStore, Handler uiHandler) {
        this.messagesLoader = messagesLoader;
        this.preferenceStore = preferenceStore;
        this.uiHandler = uiHandler;
        this.state = LoadingState.INITIAL_LOAD;
        this.preferenceStore.listenToPreferenceChanges(new PokeCallbacksListener());
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
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                callbacks.listInvalidated(conversationList);
            }
        });
    }

    private void postLoading(final Callbacks callbacks) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                callbacks.listLoading();
            }
        });
    }

    private void postLoaded(final Callbacks callbacks, final List<Conversation> conversationList) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                callbacks.listLoaded(conversationList);
            }
        });
    }

    public void reloadConversations() {
        state = LoadingState.INVALIDATED;
        for (Callbacks callbacks : callbacksList) {
            updateCallback(callbacks);
        }
        messagesLoader.loadConversationList(new ConversationListListener() {
            @Override
            public void onConversationListLoaded(List<Conversation> conversations) {
                state = LoadingState.LOADED;
                for (Callbacks callbacks : callbacksList) {
                    postLoaded(callbacks, conversations);
                }
                updateInternalList(conversations);
            }
        }, preferenceStore.getConversationSort());
    }

    private void updateInternalList(List<Conversation> conversations) {
        conversationList.clear();
        conversationList.addAll(conversations);
    }

    public interface Callbacks {
        void listLoading();

        void listLoaded(List<Conversation> conversations);

        void listInvalidated(List<Conversation> invalidatedList);
    }

    private enum LoadingState {
        INITIAL_LOAD, LOADED, INVALIDATED
    }

    private class PokeCallbacksListener implements PreferenceStore.PreferenceChangedListener {
        @Override
        public void preferenceChanged(String key) {
            for (Callbacks callbacks : callbacksList) {
                updateCallback(callbacks);
            }
        }
    }
}

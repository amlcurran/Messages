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

import com.amlcurran.messages.UpdateNotificationListener;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.notifications.Notifier;

import java.util.ArrayList;
import java.util.List;

public class ConversationList {

    private final MessagesLoader messagesLoader;
    private final Notifier notifier;
    private final PreferenceStore preferenceStore;
    private List<Callbacks> callbacksList = new ArrayList<Callbacks>();

    public ConversationList(MessagesLoader messagesLoader, Notifier notifier, PreferenceStore preferenceStore) {
        this.messagesLoader = messagesLoader;
        this.notifier = notifier;
        this.preferenceStore = preferenceStore;
    }

    private void addCallbacks(Callbacks callbacks) {
        callbacksList.add(callbacks);
        updateCallback(callbacks);
    }

    private void removeCallbacks(Callbacks callbacks) {
        callbacksList.remove(callbacks);
    }

    private void updateCallback(Callbacks callbacks) {

    }

    public void reloadConversations() {
        messagesLoader.loadConversationList(
                new UpdateNotificationListener(notifier), preferenceStore.getConversationSort());
    }

    private interface Callbacks {
        void listLoading();
        void listLoaded(List<Conversation> conversations);
        void listInvalidated();
    }

}

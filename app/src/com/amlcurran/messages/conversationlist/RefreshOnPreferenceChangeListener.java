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
import com.amlcurran.messages.core.preferences.PreferenceListener;

class RefreshOnPreferenceChangeListener implements PreferenceListener.ChangeListener {
    private final ConversationListListener conversationListViewController;
    private final ConversationList conversationList;

    public RefreshOnPreferenceChangeListener(ConversationListListener conversationListViewController, ConversationList conversationList) {
        this.conversationListViewController = conversationListViewController;
        this.conversationList = conversationList;
    }

    @Override
    public void preferenceChanged(String requestKey) {
        conversationList.reloadConversations(conversationListViewController);
    }
}

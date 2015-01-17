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

package com.amlcurran.messages;

import com.amlcurran.messages.core.conversationlist.ConversationList;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.conversationlist.SortPositions;
import com.amlcurran.messages.notifications.Notifier;

import java.util.List;

public class UpdateNotificationListener implements ConversationList.Callbacks {
    private Notifier notifier;

    public UpdateNotificationListener(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void listLoading() {
        
    }

    @Override
    public void listLoaded(List<Conversation> conversations) {
        notifier.updateUnreadNotification();
    }

    @Override
    public void listInvalidated(List<Conversation> invalidatedList) {

    }

    @Override
    public void conversationDeleted(Conversation deletedConversation, List<Conversation> conversationList) {
        if (!deletedConversation.isRead()) {
            notifier.updateUnreadNotification();
        }
    }

    @Override
    public void conversationMarkedUnread(Conversation conversation, List<Conversation> conversationList, SortPositions sortPositions) {
        notifier.updateUnreadNotification();
    }

}

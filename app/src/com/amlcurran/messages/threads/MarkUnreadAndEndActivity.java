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

package com.amlcurran.messages.threads;

import android.app.Activity;

import com.amlcurran.messages.core.conversationlist.ConversationList;
import com.amlcurran.messages.core.loaders.ConversationListChangeListener;
import com.amlcurran.messages.loaders.MessagesLoader;

import java.util.Collections;

public class MarkUnreadAndEndActivity implements UnreadViewCallback {
    private final Activity activity;
    private final MessagesLoader messagesLoader;
    private final ConversationList conversationList;

    public MarkUnreadAndEndActivity(Activity activity, MessagesLoader messagesLoader, ConversationList conversationList) {
        this.activity = activity;
        this.messagesLoader = messagesLoader;
        this.conversationList = conversationList;
    }

    @Override
    public void markUnread(String threadId) {
        messagesLoader.markThreadAsUnread(Collections.singletonList(threadId), new ConversationListChangeListener() {
            @Override
            public void listChanged() {
                conversationList.reloadConversations();
            }
        });
        activity.finish();
    }
}

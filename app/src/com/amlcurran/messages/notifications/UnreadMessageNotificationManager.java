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

package com.amlcurran.messages.notifications;

import android.content.Context;

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.Task;

import java.util.ArrayList;
import java.util.List;

import static com.espian.utils.Verbose.not;

public class UnreadMessageNotificationManager {

    private final Context context;
    private final MessagesLoader loader;
    private final List<SmsMessage> unreadMessageList;

    public UnreadMessageNotificationManager(Context context, MessagesLoader loader) {
        this.context = context;
        this.loader = loader;
        this.unreadMessageList = new ArrayList<SmsMessage>();
    }

    public void update() {
        Task getUnreadMessages = loader.loadUnreadMessages(new ThreadListener() {

            @Override
            public void onThreadLoaded(List<SmsMessage> messageList) {
                List<SmsMessage> newMessages = getNewMessages(messageList);
                unreadMessageList.clear();
                unreadMessageList.addAll(messageList);
                for (SmsMessage message : newMessages) {
                    MessagesLog.d(UnreadMessageNotificationManager.this, MessagesLog.format(message));
                }
                MessagesLog.d(UnreadMessageNotificationManager.this, "Unread messages: " + messageList.size());
            }

        });
    }

    private List<SmsMessage> getNewMessages(List<SmsMessage> messageList) {
        List<SmsMessage> newMessages = new ArrayList<SmsMessage>();
        for (SmsMessage message : messageList) {
            if (not(unreadMessageList.contains(message))) {
                newMessages.add(message);
            }
        }
        return newMessages;
    }

}

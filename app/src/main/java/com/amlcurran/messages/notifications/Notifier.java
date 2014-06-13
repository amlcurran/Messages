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

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.amlcurran.messages.MessagesApp;
import com.amlcurran.messages.PreferenceStore;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.data.InFlightSmsMessage;

import java.util.List;

public class Notifier {

    private static final int NOTIFICATION_UNREAD_MESSAGES = 22;
    private static final int NOTIFICATION_SEND_ERROR = 44;
    private static final int NOTIFICATION_MMS_ERROR = 66;
    private final NotificationManager notificationManager;
    private final Context context;
    private final NotificationBuilder notificationBuilder;

    public Notifier(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.notificationBuilder = new NotificationBuilder(context, new PreferenceStore(context));
    }

    public void updateUnreadNotification() {
        MessagesApp.getMessagesLoader(context).loadUnreadConversationList(new ConversationListListener() {
            @Override
            public void onConversationListLoaded(final List<Conversation> conversations) {
                Notification notification = notificationBuilder.buildUnreadNotification(conversations);
                notificationManager.notify(NOTIFICATION_UNREAD_MESSAGES, notification);
            }
        });
    }

    public void clearNewMessagesNotification() {
        notificationManager.cancel(NOTIFICATION_UNREAD_MESSAGES);
    }

    public void showSendError(InFlightSmsMessage message) {
        notificationManager.notify(NOTIFICATION_SEND_ERROR, notificationBuilder.buildFailureToSendNotification(message));
    }

    public void showMmsError() {
        notificationManager.notify(NOTIFICATION_MMS_ERROR, notificationBuilder.buildMmsErrorNotification());
    }
}

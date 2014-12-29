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
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationManagerCompat;

import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.preferences.SharedPreferenceStore;

import java.util.ArrayList;
import java.util.List;

import static com.espian.utils.Verbose.not;

public class Notifier {

    private static final int NOTIFICATION_UNREAD_MESSAGES = 22;
    private static final int NOTIFICATION_SEND_ERROR = 44;
    private static final int NOTIFICATION_MMS_ERROR = 66;
    public static final String ACTION_VIEW_CONVERSATION = "com.amlcurran.messages.notification.VIEW_CONVERSATION";
    private final NotificationManagerCompat notificationManager;
    private final NotificationBuilder notificationBuilder;
    private final MessagesLoader loader;
    private final PreferenceStore preferenceStore;
    private final ArrayList<Conversation> postedConversations;

    public Notifier(Context context) {
        this.preferenceStore = new SharedPreferenceStore(context);
        this.loader = SingletonManager.getMessagesLoader(context);
        this.notificationManager = NotificationManagerCompat.from(context);
        this.notificationBuilder = new NotificationBuilder(context, new SharedPreferenceStore(context));
        this.postedConversations = new ArrayList<Conversation>();
    }

    public void updateUnreadNotification() {
        if (preferenceStore.showNotifications()) {
            //unreadMessageNotificationManager.update();
            UnreadNotificationManager conversationListListener = new UnreadNotificationManager();
            loader.loadUnreadConversationList(conversationListListener);
        }
    }

    public void clearNewMessagesNotification() {
        notificationManager.cancel(NOTIFICATION_UNREAD_MESSAGES);
    }

    public void showSendError(InFlightSmsMessage message) {
        if (preferenceStore.showNotifications()) {
            notificationManager.notify(NOTIFICATION_SEND_ERROR, notificationBuilder.buildFailureToSendNotification(message));
        }
    }

    public void showMmsError() {
        notificationManager.notify(NOTIFICATION_MMS_ERROR, notificationBuilder.buildMmsErrorNotification());
    }

    public void clearFailureToSendNotification() {
        notificationManager.cancel(NOTIFICATION_SEND_ERROR);
    }

    private class UnreadNotificationManager implements ConversationListListener {

        @Override
        public void onConversationListLoaded(final List<Conversation> conversations) {
            updatePostedConversations(conversations);
            if (conversations.size() == 0) {
                clearNewMessagesNotification();
            } else if (conversations.size() == 1) {
                // Load the contact photo as well
                Conversation singleConvo = conversations.get(0);
                List<Conversation> newConversations = getNewConversations(conversations);
                loader.loadPhoto(singleConvo.getContact(), new PostUnreadWhenLoadedListener(conversations, newConversations));
            } else {
                List<Conversation> newConversations = getNewConversations(conversations);
                postUnreadNotification(conversations, null, newConversations);
            }
        }

        private class PostUnreadWhenLoadedListener implements PhotoLoadListener {
            private final List<Conversation> conversations;
            private final List<Conversation> newConversations;

            public PostUnreadWhenLoadedListener(List<Conversation> conversations, List<Conversation> newConversations) {
                this.conversations = conversations;
                this.newConversations = newConversations;
            }

            @Override
            public void photoLoaded(Bitmap photo) {
                postUnreadNotification(conversations, photo, newConversations);
            }

            @Override
            public void photoLoadedFromCache(Bitmap photo) {
                postUnreadNotification(conversations, photo, newConversations);
            }

            @Override
            public void beforePhotoLoad(Contact contact) {

            }
        }

        private void postUnreadNotification(List<Conversation> conversations, Bitmap photo, List<Conversation> newConversations) {
            List<Notification> notifications = notificationBuilder.buildUnreadNotification(conversations, photo, newConversations);
            for (int i = 0; i < notifications.size(); i++) {
                notificationManager.notify(NOTIFICATION_UNREAD_MESSAGES + i, notifications.get(i));
            }
        }

    }

    private List<Conversation> getNewConversations(List<Conversation> unreadConversations) {
        List<Conversation> newUnreadConversations = new ArrayList<Conversation>();
        for (Conversation conversation : unreadConversations) {
            if (not(postedConversations.contains(conversation))) {
                newUnreadConversations.add(conversation);
                postedConversations.add(conversation);
            }
        }
        return newUnreadConversations;
    }

    private void updatePostedConversations(List<Conversation> unreadConversations) {
        List<Conversation> newPostedConversations = new ArrayList<Conversation>();
        for (Conversation postedConversation : postedConversations) {
            if (unreadConversations.contains(postedConversation)) {
                newPostedConversations.add(postedConversation);
            }
        }
        postedConversations.clear();
        postedConversations.addAll(newPostedConversations);
    }
}

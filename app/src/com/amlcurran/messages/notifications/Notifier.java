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
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.conversationlist.ConversationLoader;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.core.loaders.OnContactQueryListener;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.loaders.photos.PhotoLoader;
import com.amlcurran.messages.preferences.SharedPreferenceStore;

import java.util.ArrayList;
import java.util.List;

public class Notifier {

    private static final int NOTIFICATION_UNREAD_MESSAGES_SUMMARY = 21;
    private static final int NOTIFICATION_UNREAD_MESSAGES = 22;
    private static final int NOTIFICATION_SEND_ERROR = 44;
    private static final int NOTIFICATION_MMS_ERROR = 66;
    private static final int NOTIFICATION_RESENT = 42;
    public static final String ACTION_VIEW_CONVERSATION = "com.amlcurran.messages.notification.VIEW_CONVERSATION";
    private final NotificationManagerCompat notificationManager;
    private final NotificationBuilder notificationBuilder;
    private final ConversationLoader conversationLoader;
    private final PreferenceStore preferenceStore;
    private final ArrayList<Conversation> postedConversations;
    private final PhotoLoader photoLoader;
    private final MessagesLoader messagesLoader;

    public Notifier(Context context) {
        this.preferenceStore = new SharedPreferenceStore(context);
        this.conversationLoader = SingletonManager.getConversationLoader(context);
        this.messagesLoader = SingletonManager.getMessagesLoader(context);
        this.photoLoader = SingletonManager.getPhotoLoader(context);
        this.notificationManager = NotificationManagerCompat.from(context);
        this.notificationBuilder = new NotificationBuilder(context, new SharedPreferenceStore(context));
        this.postedConversations = new ArrayList<Conversation>();
    }

    public void updateUnreadNotification() {
        if (preferenceStore.showNotifications()) {
            UnreadNotificationManager conversationListListener = new UnreadNotificationManager();
            conversationLoader.loadUnreadConversationList(conversationListListener);
        }
    }

    public void clearNewMessagesNotification() {
        notificationManager.cancel(NOTIFICATION_UNREAD_MESSAGES);
    }

    public void showSendError(SmsMessage message, Contact contact) {
        if (preferenceStore.showNotifications()) {
            notificationManager.notify(NOTIFICATION_SEND_ERROR, notificationBuilder.buildFailureToSendNotification(message, contact));
        }
    }

    public void showMmsError() {
        notificationManager.notify(NOTIFICATION_MMS_ERROR, notificationBuilder.buildMmsErrorNotification());
    }

    public void clearFailureToSendNotification() {
        notificationManager.cancel(NOTIFICATION_SEND_ERROR);
    }

    public void showResentMessage(long threadId) {
        notificationManager.notify(NOTIFICATION_RESENT, notificationBuilder.buildResentNotification(threadId));
    }

    public void addNewMessageNotification(final SmsMessage smsMessage) {
        messagesLoader.queryContact(smsMessage.getAddress(), new OnContactQueryListener() {
            @Override
            public void contactLoaded(final Contact contact) {
                photoLoader.loadPhoto(contact, new PhotoLoadListener() {
                    @Override
                    public void photoLoaded(Bitmap photo) {
                        notificationManager.notify(smsMessage.getThreadId().hashCode(), notificationBuilder.buildUnreadMessageNotification(photo, contact, smsMessage));
                    }

                    @Override
                    public void photoLoadedFromCache(Bitmap photo) {
                        notificationManager.notify(smsMessage.getThreadId().hashCode(), notificationBuilder.buildUnreadMessageNotification(photo, contact, smsMessage));
                    }

                    @Override
                    public void beforePhotoLoad(Contact contact) {

                    }
                });
            }
        });
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
                removeSummaryNotification();
                photoLoader.loadPhoto(singleConvo.getContact(), new PostUnreadWhenLoadedListener(conversations));
            } else {
                postUnreadNotification(conversations, null);
            }
        }

        private class PostUnreadWhenLoadedListener implements PhotoLoadListener {
            private final List<Conversation> conversations;

            public PostUnreadWhenLoadedListener(List<Conversation> conversations) {
                this.conversations = conversations;
            }

            @Override
            public void photoLoaded(Bitmap photo) {
                postUnreadNotification(conversations, photo);
            }

            @Override
            public void photoLoadedFromCache(Bitmap photo) {
                postUnreadNotification(conversations, photo);
            }

            @Override
            public void beforePhotoLoad(Contact contact) {

            }
        }

        private void postUnreadNotification(List<Conversation> conversations, @Nullable Bitmap photo) {
            Notification notification = notificationBuilder.buildUnreadNotification(conversations, photo);
            if (notification != null) {
                notificationManager.notify(NOTIFICATION_UNREAD_MESSAGES_SUMMARY, notification);
            }
        }

    }

    private void removeSummaryNotification() {
        notificationManager.cancel(NOTIFICATION_UNREAD_MESSAGES_SUMMARY);
    }

    private void updatePostedConversations(List<Conversation> unreadConversations) {
        List<Conversation> newPostedConversations = new ArrayList<>();
        for (Conversation postedConversation : postedConversations) {
            if (unreadConversations.contains(postedConversation)) {
                newPostedConversations.add(postedConversation);
            }
        }
        postedConversations.clear();
        postedConversations.addAll(newPostedConversations);
    }
}

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
import android.support.v4.app.NotificationCompat;

import com.amlcurran.messages.analysis.MessageAnalyser;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.Time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnreadNotificationBuilder {
    private final Context context;
    private final NotificationBuilder notificationBuilder;
    private final StyledTextFactory styledTextFactory;
    private final NotificationActionBuilder actionBuilder;
    private final NotificationIntentFactory notificationIntentFactory;

    public UnreadNotificationBuilder(Context context, NotificationBuilder notificationBuilder, StyledTextFactory styledTextFactory, NotificationActionBuilder actionBuilder, NotificationIntentFactory notificationIntentFactory) {
        this.context = context;
        this.notificationBuilder = notificationBuilder;
        this.styledTextFactory = styledTextFactory;
        this.actionBuilder = actionBuilder;
        this.notificationIntentFactory = notificationIntentFactory;
    }

    public List<Notification> buildUnreadNotification(List<Conversation> conversations, Bitmap photo, List<Conversation> newConversations) {
        CharSequence ticker = tickerText(conversations, newConversations);
        boolean hasNewConversations = newConversations.size() > 0;
        if (conversations.size() == 1) {
            NotificationCompat.Builder builder = buildSingleUnreadNotification(conversations.get(0), photo, hasNewConversations, ticker);
            return Collections.singletonList(builder.build());
        } else {
            return buildMultipleUnreadNotification(conversations, hasNewConversations, ticker);
        }
    }

    CharSequence tickerText(List<Conversation> conversations, List<Conversation> newConversations) {
        if (newConversations.size() == 1) {
            return styledTextFactory.buildTicker(newConversations.get(0));
        } else if (newConversations.size() == 0) {
            return null;
        } else {
            return styledTextFactory.buildListSummary(context, conversations);
        }
    }

    List<Notification> buildMultipleUnreadNotification(List<Conversation> conversations, boolean fromNewMessage, CharSequence ticker) {
        List<Notification> notifications = new ArrayList<Notification>();
        notifications.add(buildMultipleSummaryNotification(conversations, fromNewMessage, ticker));
        for (Conversation conversation : conversations) {
            NotificationCompat.Builder builder = buildSingleUnreadNotification(conversation, null, fromNewMessage, ticker);
            builder.setGroup(NotificationBuilder.UNREAD_MESSAGES);
            notifications.add(builder.build());
        }
        return notifications;
    }

    Notification buildMultipleSummaryNotification(List<Conversation> conversations, boolean fromNewMessage, CharSequence ticker) {
        Time latestMessageTime = getLatestMessageTime(conversations);
        NotificationCompat.Action markReadAction = actionBuilder.buildMultipleMarkReadAction(conversations);

        NotificationCompat.Builder defaultBuilder = notificationBuilder.getDefaultBuilder(fromNewMessage);
        NotificationBinder notificationBinder = new NotificationBinder(defaultBuilder);
        notificationBinder.setStyle(buildInboxStyle(conversations));

        return notificationBinder.getBaseBuilder()
                .addAction(markReadAction)
                .setTicker(ticker)
                .setGroup(NotificationBuilder.UNREAD_MESSAGES)
                .setGroupSummary(true)
                .setContentText(styledTextFactory.buildSenderList(conversations))
                .setContentTitle(styledTextFactory.buildListSummary(context, conversations))
                .setWhen(latestMessageTime.toMillis())
                .build();
    }

    Time getLatestMessageTime(List<Conversation> conversations) {
        Time latest = Time.fromMillis(0);
        for (Conversation conversation : conversations) {
            if (conversation.getTimeOfLastMessage().isLaterThan(latest)) {
                latest = conversation.getTimeOfLastMessage();
            }
        }
        return latest;
    }

    NotificationCompat.Style buildInboxStyle(List<Conversation> conversations) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (Conversation conversation : conversations) {
            inboxStyle.addLine(styledTextFactory.getInboxLine(conversation));
        }
        return inboxStyle;
    }

    NotificationCompat.Builder buildSingleUnreadNotification(Conversation conversation, Bitmap photo, boolean fromNewMessage, CharSequence ticker) {
        NotificationCompat.Action voiceInputAction = actionBuilder.buildReplyAction(conversation);
        NotificationCompat.Action singleUnreadAction = actionBuilder.buildSingleMarkReadAction(conversation);
        NotificationCompat.Action callAction = actionBuilder.call(conversation.getContact());
        NotificationCompat.Extender extender = new NotificationCompat.WearableExtender().addAction(voiceInputAction);
        NotificationCompat.Builder builder = notificationBuilder.getDefaultBuilder(fromNewMessage);

        // use some cheeky message analysis
        MessageAnalyser analyser = new MessageAnalyser(conversation.getSummaryText());
        if (analyser.hasLink()) {
            NotificationCompat.Action linkAction = actionBuilder.buildLinkAction(analyser.getLink());
            builder.addAction(linkAction);
        }

        NotificationBinder binder = new NotificationBinder(builder);
        binder.setStyle(buildBigStyle(conversation));
        binder.setExtender(extender);

        Contact contact = conversation.getContact();

        return binder.getBaseBuilder()
                .addAction(singleUnreadAction)
                .addAction(callAction)
                .setTicker(ticker)
                .addPerson(NotificationBuilder.getContactUri(contact))
                .setContentTitle(conversation.getContact().getDisplayName())
                .setLargeIcon(photo)
                .setContentIntent(notificationIntentFactory.createViewConversationIntent(conversation))
                .setContentText(conversation.getSummaryText())
                .setWhen(conversation.getTimeOfLastMessage().toMillis());
    }

    NotificationCompat.Style buildBigStyle(Conversation conversation) {
        return new NotificationCompat.BigTextStyle()
                .bigText(conversation.getSummaryText())
                .setBigContentTitle(conversation.getContact().getDisplayName());
    }
}
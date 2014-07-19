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

import com.amlcurran.messages.R;
import com.amlcurran.messages.analysis.MessageAnalyser;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.preferences.PreferenceStore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class NotificationBuilder {
    private static final long[] VIBRATE_PATTERN = new long[]{0, 200};
    private static final String UNREAD_MESSAGES = "unreads";
    private final NotificationIntentFactory notificationIntentFactory;
    private final StyledTextFactory styledTextFactory;
    private final NotificationActionBuilder actionBuilder;
    private Context context;
    private final PreferenceStore preferenceStore;

    public NotificationBuilder(Context context, PreferenceStore preferenceStore) {
        this.context = context;
        this.preferenceStore = preferenceStore;
        this.notificationIntentFactory = new NotificationIntentFactory(context);
        this.styledTextFactory = new StyledTextFactory();
        this.actionBuilder = new NotificationActionBuilder(context);
    }

    public List<Notification> buildUnreadNotification(List<Conversation> conversations, Bitmap photo, boolean fromNewMessage) {
        if (conversations.size() == 1) {
            return Collections.singletonList(buildSingleUnreadNotification(conversations.get(0), photo, fromNewMessage));
        } else {
            return buildMultipleUnreadNotification(conversations, fromNewMessage);
        }
    }

    private List<Notification> buildMultipleUnreadNotification(List<Conversation> conversations, boolean fromNewMessage) {
        List<Notification> notifications = new ArrayList<Notification>();
        notifications.add(buildMultipleSummaryNotification(conversations, fromNewMessage));
        for (Conversation conversation : conversations) {
            notifications.add(buildSingleUnreadNotification(conversation, null, fromNewMessage));
        }
        return notifications;
    }

    private Notification buildMultipleSummaryNotification(List<Conversation> conversations, boolean fromNewMessage) {
        long timestampMillis = Calendar.getInstance().getTimeInMillis();
        CharSequence ticker = fromNewMessage ? styledTextFactory.buildTicker(conversations.get(0)) : styledTextFactory.buildListSummary(context, conversations);
        NotificationCompat.Action markReadAction = actionBuilder.buildMultipleMarkReadAction(conversations);
        return getDefaultBuilder(fromNewMessage)
                .addAction(markReadAction)
                .setTicker(ticker)
                .setGroup(UNREAD_MESSAGES)
                .setGroupSummary(true)
                .setStyle(buildInboxStyle(conversations))
                .setContentText(styledTextFactory.buildSenderList(conversations))
                .setContentTitle(styledTextFactory.buildListSummary(context, conversations))
                .setWhen(timestampMillis)
                .build();
    }

    private NotificationCompat.Style buildInboxStyle(List<Conversation> conversations) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (Conversation conversation : conversations) {
            inboxStyle.addLine(styledTextFactory.getInboxLine(conversation));
        }
        return inboxStyle;
    }

    private Notification buildSingleUnreadNotification(Conversation conversation, Bitmap photo, boolean fromNewMessage) {
        long timestampMillis = Calendar.getInstance().getTimeInMillis();
        CharSequence tickerText = fromNewMessage ? styledTextFactory.buildTicker(conversation) : styledTextFactory.buildListSummary(context, Collections.singletonList(conversation));
        NotificationCompat.Action voiceInputAction = actionBuilder.buildReplyAction(conversation);
        NotificationCompat.Action singleUnreadAction = actionBuilder.buildSingleMarkReadAction(conversation);
        NotificationCompat.Action callAction = actionBuilder.call(conversation.getContact());
        NotificationCompat.Extender extender = new NotificationCompat.WearableExtender().addAction(voiceInputAction);
        NotificationCompat.Builder builder = getDefaultBuilder(fromNewMessage);

        // use some cheeky message analysis
        MessageAnalyser analyser = new MessageAnalyser(conversation.getBody());
        if (analyser.hasLink()) {
            NotificationCompat.Action linkAction = actionBuilder.buildLinkAction(analyser.getLink());
            builder.addAction(linkAction);
        }

        return builder
                .addAction(singleUnreadAction)
                .addAction(callAction)
                .setTicker(tickerText)
                .setGroup(UNREAD_MESSAGES)
                .setContentTitle(conversation.getContact().getDisplayName())
                .setLargeIcon(photo)
                .setContentIntent(notificationIntentFactory.createViewConversationIntent(conversation))
                .setContentText(conversation.getBody())
                .setStyle(buildBigStyle(conversation))
                .setWhen(timestampMillis)
                .extend(extender)
                .build();
    }

    private static NotificationCompat.Style buildBigStyle(Conversation conversation) {
        return new NotificationCompat.BigTextStyle()
                .bigText(conversation.getBody())
                .setBigContentTitle(conversation.getContact().getDisplayName());
    }

    private NotificationCompat.Builder getDefaultBuilder() {
        return getDefaultBuilder(true);
    }

    private NotificationCompat.Builder getDefaultBuilder(boolean shouldSoundAndVibrate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context)
                .setContentIntent(notificationIntentFactory.createLaunchActivityIntent())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notify_sms)
                .setDefaults(Notification.DEFAULT_LIGHTS);

        if (shouldSoundAndVibrate) {
            builder.setSound(preferenceStore.getRingtoneUri());
            builder.setVibrate(VIBRATE_PATTERN);
        }

        return builder;
    }

    public Notification buildFailureToSendNotification(InFlightSmsMessage message) {
        return getDefaultBuilder()
                .setContentTitle(string(R.string.failed_to_send_message))
                .setTicker(string(R.string.failed_to_send_message))
                .setContentText(context.getString(R.string.couldnt_send_to, message.getPhoneNumber().toString()))
                .addAction(R.drawable.ic_action_send_holo, string(R.string.resend), notificationIntentFactory.createResendIntent(message))
                .setSmallIcon(R.drawable.ic_notify_error)
                .build();
    }

    private String string(int resId) {
        return context.getString(resId);
    }

    public Notification buildMmsErrorNotification() {
        return getDefaultBuilder()
                .setContentTitle(string(R.string.mms_error_title))
                .setTicker(string(R.string.mms_error_title))
                .setContentText(string(R.string.mms_error_text))
                .setSmallIcon(R.drawable.ic_notify_error)
                .build();
    }
}

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
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;

import com.amlcurran.messages.R;
import com.amlcurran.messages.analysis.MessageAnalyser;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.telephony.SmsManagerOutputPort;

import java.util.Collections;
import java.util.List;

public class NotificationBuilder {
    private static final long[] VIBRATE_PATTERN = new long[]{0, 200};
    static final String UNREAD_MESSAGES = "unreads";
    private final NotificationIntentFactory notificationIntentFactory;
    private final Context context;
    private final PreferenceStore preferenceStore;
    private final int notificationColor;
    private final StyledTextFactory styledTextFactory;
    private final NotificationActionBuilder actionBuilder;

    public NotificationBuilder(Context context, PreferenceStore preferenceStore) {
        this.context = context;
        this.preferenceStore = preferenceStore;
        this.notificationIntentFactory = new NotificationIntentFactory(context);
        this.actionBuilder = new NotificationActionBuilder(context);
        this.notificationColor = context.getResources().getColor(R.color.theme_colour);
        this.styledTextFactory = new StyledTextFactory();
    }

    public List<Notification> buildUnreadSummaryNotification(List<Conversation> conversations) {
        CharSequence ticker = styledTextFactory.buildListSummary(context, conversations);
        if (conversations.size() == 1) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(buildMultipleSummaryNotification(conversations, ticker));
        }
    }

    static String getContactUri(Contact contact) {
        return String.valueOf(ContactsContract.Contacts.getLookupUri(contact.getContactId(), contact.getLookupKey()));
    }

    NotificationCompat.Builder getDefaultBuilder() {
        return getDefaultBuilder(true);
    }

    NotificationCompat.Builder getDefaultBuilder(boolean shouldSoundAndVibrate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context)
                .setContentIntent(notificationIntentFactory.createLaunchActivityIntent())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notify_sms)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(notificationColor)
                .setDefaults(Notification.DEFAULT_LIGHTS);

        if (shouldSoundAndVibrate) {
            if (preferenceStore.hasRingtoneUri()) {
                builder.setSound(Uri.parse(preferenceStore.getRingtoneUri().toString()));
            } else {
                builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
            }
            builder.setVibrate(VIBRATE_PATTERN);
        }

        return builder;
    }

    public Notification buildFailureToSendNotification(SmsMessage message, Contact contact) {
        return getDefaultBuilder()
                .setContentTitle(string(R.string.failed_to_send_message))
                .setTicker(string(R.string.failed_to_send_message))
                .setContentText(context.getString(R.string.couldnt_send_to, contact.getDisplayName()))
                .addAction(R.drawable.ic_send, string(R.string.resend), SmsManagerOutputPort.resendPendingIntent(message, context))
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

    public Notification buildUnreadMessageNotification(Bitmap photo, Contact contact, SmsMessage smsMessage) {
        NotificationCompat.Action singleUnreadAction = actionBuilder.buildSingleMarkReadAction(smsMessage.getThreadId());
        NotificationCompat.Action callAction = actionBuilder.call(contact);
        NotificationCompat.Builder builder = getDefaultBuilder();

//        analyseMessage(conversation, builder);
//        enableVoiceReply(conversation, builder);

        return builder.addAction(callAction)
                .addAction(singleUnreadAction)
                .setTicker(smsMessage.getBody())
                .setPriority(Notification.PRIORITY_HIGH)
                .addPerson(NotificationBuilder.getContactUri(contact))
                .setContentTitle(contact.getDisplayName())
                .setGroup(NotificationBuilder.UNREAD_MESSAGES)
                .setLargeIcon(photo)
                .setContentIntent(notificationIntentFactory.createViewConversationIntent(smsMessage))
                .setContentText(smsMessage.getBody())
                .setWhen(smsMessage.getTimestamp().toMillis())
                .build();
    }

    Notification buildMultipleSummaryNotification(List<Conversation> conversations, CharSequence ticker) {
        Time latestMessageTime = MessageAnalyser.getLatestMessageTime(conversations);
        NotificationCompat.Action markReadAction = actionBuilder.buildMultipleMarkReadAction(conversations);

        NotificationCompat.Builder defaultBuilder = getDefaultBuilder(false);
        NotificationBinder notificationBinder = new NotificationBinder(defaultBuilder);
        notificationBinder.setStyle(buildInboxStyle(conversations));

        return notificationBinder.getBaseBuilder()
                .addAction(markReadAction)
                .setTicker(ticker)
                .setGroup(UNREAD_MESSAGES)
                .setPriority(Notification.PRIORITY_HIGH)
                .setGroupSummary(true)
                .setContentText(styledTextFactory.buildSenderList(conversations))
                .setContentTitle(styledTextFactory.buildListSummary(context, conversations))
                .setWhen(latestMessageTime.toMillis())
                .build();
    }

    NotificationCompat.Style buildInboxStyle(List<Conversation> conversations) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (Conversation conversation : conversations) {
            inboxStyle.addLine(styledTextFactory.getInboxLine(conversation));
        }
        return inboxStyle;
    }
}

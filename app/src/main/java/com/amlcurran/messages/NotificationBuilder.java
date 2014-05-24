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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.amlcurran.messages.data.Conversation;
import com.amlcurran.messages.data.SmsMessage;

import java.util.Calendar;
import java.util.List;

public class NotificationBuilder {
    private static final long[] VIBRATE_PATTERN = new long[]{200};
    private Context context;

    public NotificationBuilder(Context context) {
        this.context = context;
    }

    public Notification buildUnreadNotification(List<Conversation> conversations) {
        if (conversations.size() == 1) {
            return buildSingleUnreadNotification(conversations.get(0));
        } else {
            return buildMultipleUnreadNotification(conversations);
        }
    }

    private Notification buildMultipleUnreadNotification(List<Conversation> conversations) {
        long timestampMillis = Calendar.getInstance().getTimeInMillis();
        return getDefaultBuilder()
                .setTicker(buildListSummary(conversations))
                .setStyle(buildInboxStyle(conversations))
                .setContentText(buildSenderList(conversations))
                .setContentTitle(buildListSummary(conversations))
                .setWhen(timestampMillis)
                .build();
    }

    private static CharSequence buildSenderList(List<Conversation> conversations) {
        String result = "";
        for (Conversation conversation : conversations) {
            result += conversation.getName() + ", ";
        }
        return result.substring(0, result.length() - 2);
    }

    private static CharSequence buildListSummary(List<Conversation> conversations) {
        return String.format("%1$d new messages", conversations.size());
    }

    private static Notification.Style buildInboxStyle(List<Conversation> conversations) {
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
        for (Conversation conversation : conversations) {
            inboxStyle.addLine(getInboxLine(conversation));
        }
        return inboxStyle;
    }

    private static CharSequence getInboxLine(Conversation conversation) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(conversation.getName());
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#cccccc"));
        builder.setSpan(colorSpan, 0, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(" ");
        builder.append(conversation.getBody());
        return builder;
    }

    private Notification buildSingleUnreadNotification(Conversation conversation) {
        long timestampMillis = Calendar.getInstance().getTimeInMillis();
        return getDefaultBuilder()
                .setTicker(buildTicker(conversation))
                .setContentTitle(conversation.getName())
                .setContentText(conversation.getBody())
                .setStyle(buildBigStyle(conversation))
                .setWhen(timestampMillis)
                .build();
    }

    private static Notification.Style buildBigStyle(Conversation conversation) {
        return new Notification.BigTextStyle()
                .bigText(conversation.getBody())
                .setBigContentTitle(conversation.getName());
    }

    private static CharSequence buildTicker(Conversation conversation) {
        SpannableStringBuilder builder = new SpannableStringBuilder(conversation.getName() + ": ");
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(conversation.getBody());
        return builder;
    }

    private Notification.Builder getDefaultBuilder() {
        Intent intent = new Intent(this.context, MessagesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
        return new Notification.Builder(this.context)
                .setContentIntent(pendingIntent)
                .setVibrate(VIBRATE_PATTERN)
                .setSmallIcon(R.drawable.ic_notify_sms);
    }

    public Notification buildErrorNotification(SmsMessage message) {
        return getDefaultBuilder()
                .setContentTitle(string(R.string.failed_to_send_message))
                .setTicker(string(R.string.failed_to_send_message))
                .setContentText(context.getString(R.string.couldnt_send_to, message.getAddress()))
                .setSmallIcon(R.drawable.ic_notify_error)
                .build();
    }

    private String string(int resId) {
        return context.getString(resId);
    }
}

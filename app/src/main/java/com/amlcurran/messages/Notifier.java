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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import com.amlcurran.messages.conversationlist.Conversation;
import com.amlcurran.messages.conversationlist.ConversationListListener;

import java.util.Calendar;
import java.util.List;

public class Notifier {

    private static final int ID_NEW_MESSAGE = 22;
    private static final long[] VIBRATE_PATTERN = new long[] { 200 };
    private final NotificationManager notificationManager;
    private final Context context;

    public Notifier(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void updateUnreadNotification() {
        MessagesApp.getMessagesLoader(context).loadUnreadConversationList(new ConversationListListener() {
            @Override
            public void onConversationListLoaded(List<Conversation> conversations) {
                for (Conversation conversation : conversations) {
                    Notification newMessageNotification = buildNotification(context, conversation.getName(),
                            conversation.getBody(), Calendar.getInstance().getTimeInMillis());
                    notificationManager.notify(conversations.hashCode(), newMessageNotification);
                }
            }
        });
    }

    public void clearNewMessagesNotification() {
        notificationManager.cancel(ID_NEW_MESSAGE);
    }

    private Notification buildNotification(Context context, String address, String body, long timestampMillis) {
        Notification.BigTextStyle style = new Notification.BigTextStyle()
                .bigText(body)
                .setBigContentTitle(address);
        return getDefaultBuilder(context)
                .setTicker(buildTicker(address, body))
                .setContentTitle(address)
                .setContentText(body)
                .setStyle(style)
                .setWhen(timestampMillis)
                .build();
    }

    private CharSequence buildTicker(String address, String body) {
        SpannableStringBuilder builder = new SpannableStringBuilder(address + ": ");
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(body);
        return builder;
    }

    private Notification.Builder getDefaultBuilder(Context context) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setVibrate(VIBRATE_PATTERN)
                .setSmallIcon(R.drawable.ic_notify_sms);
    }
}

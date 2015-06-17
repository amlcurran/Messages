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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import com.amlcurran.messages.R;
import com.amlcurran.messages.analysis.Link;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.loaders.ExecutingIntentService;
import com.amlcurran.messages.telephony.SmsManagerOutputPort;

import java.util.List;

class NotificationActionBuilder {
    private final Context context;

    public NotificationActionBuilder(Context context) {
        this.context = context;
    }

    NotificationCompat.Action buildSingleMarkReadAction(String threadId) {
        PendingIntent markReadIntent = ExecutingIntentService.markReadPendingIntentSingle(context, threadId);
        String label = context.getString(R.string.mark_as_read);
        return new NotificationCompat.Action.Builder(R.drawable.ic_mark_read, label, markReadIntent).build();
    }

    NotificationCompat.Action buildReplyAction(Conversation conversation) {
        RemoteInput remoteInput = new RemoteInput.Builder(SmsManagerOutputPort.EXTRA_VOICE_REPLY)
                .setLabel(context.getString(R.string.reply))
                .build();

        Intent replyIntent = new Intent(context, SmsManagerOutputPort.class);
        replyIntent.setAction(SmsManagerOutputPort.ACTION_SEND_REQUEST);
        replyIntent.putExtra(SmsManagerOutputPort.FROM_WEAR, true);
        replyIntent.putExtra(SmsManagerOutputPort.EXTRA_NUMBER, conversation.getAddress().flatten());
        PendingIntent replyPendingIntent = PendingIntent.getService(context, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(R.drawable.ic_wear_reply,
                context.getString(R.string.reply), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();
    }

    NotificationCompat.Action buildMultipleMarkReadAction(List<Conversation> conversations) {
        PendingIntent markReadIntent = ExecutingIntentService.markReadPendingIntent(context, conversations);
        String label = context.getString(R.string.mark_all_as_read);
        return new NotificationCompat.Action.Builder(R.drawable.ic_mark_read, label, markReadIntent).build();
    }

    public NotificationCompat.Action call(Contact contact) {
        PendingIntent callIntent = callPendingIntent(contact.getNumber());
        String label = context.getString(R.string.call);
        return new NotificationCompat.Action.Builder(R.drawable.ic_call, label, callIntent).build();
    }

    private PendingIntent callPendingIntent(PhoneNumber number) {
        Uri telUri = Uri.parse("tel:" + number.flatten());
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(telUri);
        return PendingIntent.getActivity(context, 12121, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public NotificationCompat.Action buildLinkAction(Link link) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(link.get());
        PendingIntent linkIntent = PendingIntent.getActivity(context, 12123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String label = context.getString(R.string.open_link);
        return new NotificationCompat.Action.Builder(R.drawable.ic_website, label, linkIntent).build();
    }

}
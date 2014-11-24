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
import android.os.Bundle;

import com.amlcurran.messages.MessagesActivity;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.telephony.SmsSender;
import com.amlcurran.messages.threads.ThreadActivity;

public class NotificationIntentFactory {
    private final Context context;

    public NotificationIntentFactory(Context context) {
        this.context = context;
    }

    PendingIntent createViewConversationIntent(Conversation conversation) {
        Bundle smooshed = ContactFactory.smooshContact(ContactFactory.fromAddress(conversation.getAddress().flatten()));
        Intent intent = ThreadActivity.intent(context, conversation.getThreadId(), smooshed, null);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    PendingIntent createLaunchActivityIntent() {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    PendingIntent createResendIntent(InFlightSmsMessage message) {
        return PendingIntent.getService(context, 0, SmsSender.resendMessageIntent(context, message), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public PendingIntent markRead(Conversation conversation) {
        return null;
    }
}
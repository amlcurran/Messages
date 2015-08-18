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
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.threads.ThreadActivity;

public class NotificationIntentFactory {
    private final Context context;

    public NotificationIntentFactory(Context context) {
        this.context = context;
    }

    PendingIntent createViewConversationIntent(Conversation conversation) {
        String threadId = conversation.getThreadId();
        return viewThread(threadId, conversation.getAddress());
    }

    private PendingIntent viewThread(String threadId, PhoneNumber address) {
        Bundle smooshed = ContactFactory.smooshContact(ContactFactory.fromAddress(address.flatten()));
        Intent intent = ThreadActivity.intent(context, threadId, smooshed, null);
        return PendingIntent.getActivity(context, threadId.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    PendingIntent createLaunchActivityIntent() {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    public PendingIntent markRead(Conversation conversation) {
        return null;
    }

    public PendingIntent createViewConversationIntent(SmsMessage smsMessage) {
        return viewThread(smsMessage.getThreadId(), smsMessage.getAddress());
    }
}
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

package com.amlcurran.messages.telephony;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.core.loaders.OnContactQueryListener;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.events.BroadcastEventBus;

public class SmsSentNotificationService extends IntentService {

    private static final String ACTION_MESSAGE_SENT = "message_send";
    private static final String ACTION_MESSAGE_FAILED_SEND = "failed_send";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_OUTBOX_URI = "outbox_uri";
    private final MessageRepository messageRepository;

    public SmsSentNotificationService() {
        super("SmsSendNotificationService");
        setIntentRedelivery(true);
        SmsDatabaseWriter smsDatabaseWriter = new SmsDatabaseWriter(this);
        EventBus eventBus = new BroadcastEventBus(this);
        messageRepository = new MessageRepository(smsDatabaseWriter, eventBus);
    }

    static Intent sentIntent(Context context, InFlightSmsMessage message, String outboxUri) {
        return getIntent(context, message, outboxUri, ACTION_MESSAGE_SENT);
    }

    public static Intent failedSentIntent(Context context, InFlightSmsMessage message, String outboxUri) {
        return getIntent(context, message, outboxUri, ACTION_MESSAGE_FAILED_SEND);
    }

    private static Intent getIntent(Context context, InFlightSmsMessage message, String outboxUri, String action) {
        Intent sentIntent = new Intent(context, SmsSentNotificationService.class);
        sentIntent.setAction(action);
        sentIntent.putExtra(EXTRA_MESSAGE, message);
        sentIntent.putExtra(EXTRA_OUTBOX_URI, outboxUri);
        return sentIntent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InFlightSmsMessage message = intent.getParcelableExtra(EXTRA_MESSAGE);
        Uri outboxSms = Uri.parse(intent.getStringExtra(EXTRA_OUTBOX_URI));
        if (ACTION_MESSAGE_SENT.equals(intent.getAction())) {
            messageRepository.sent(message, outboxSms);
        } else if (ACTION_MESSAGE_FAILED_SEND.equals(intent.getAction())) {
            notifyFailureToSend(message);
            messageRepository.failedToSend(message, outboxSms);
        }
    }

    private void notifyFailureToSend(final InFlightSmsMessage message) {
        SingletonManager.getMessagesLoader(this).queryContact(message.getPhoneNumber(), new OnContactQueryListener() {
            @Override
            public void contactLoaded(Contact contact) {
                SingletonManager.getNotifier(SmsSentNotificationService.this).showSendError(message, contact);
            }
        });
    }
}

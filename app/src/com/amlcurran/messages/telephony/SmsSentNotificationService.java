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

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.events.BroadcastEventBus;

public class SmsSentNotificationService extends IntentService {

    static final String EXTRA_RESULT = "result";
    private static final String ACTION_MESSAGE_SENT = "message_send";
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

    static Intent sentIntent(Context context, int resultCode, InFlightSmsMessage message, String outboxUri) {
        Intent sentIntent = new Intent(context, SmsSentNotificationService.class);
        sentIntent.setAction(ACTION_MESSAGE_SENT);
        sentIntent.putExtra(EXTRA_MESSAGE, message);
        sentIntent.putExtra(EXTRA_OUTBOX_URI, outboxUri);
        sentIntent.putExtra(EXTRA_RESULT, resultCode);
        return sentIntent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_MESSAGE_SENT.equals(intent.getAction())) {
            InFlightSmsMessage message = intent.getParcelableExtra(EXTRA_MESSAGE);
            Uri outboxSms = Uri.parse(intent.getStringExtra(EXTRA_OUTBOX_URI));
            if (sentSuccessfully(intent)) {
                messageRepository.sent(message, outboxSms);
            } else {
                notifyFailureToSend(message);
                messageRepository.failedToSend(message, outboxSms);
            }
        }
    }

    private static boolean sentSuccessfully(Intent intent) {
        return intent.getIntExtra(EXTRA_RESULT, 0) == Activity.RESULT_OK;
    }

    private void notifyFailureToSend(InFlightSmsMessage message) {
        SingletonManager.getNotifier(this).showSendError(message);
    }
}

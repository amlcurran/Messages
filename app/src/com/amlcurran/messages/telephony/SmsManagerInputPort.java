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
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.core.loaders.OnContactQueryListener;
import com.amlcurran.messages.events.BroadcastEventBus;

/**
 * This class manages the notifications received from
 * the SmsManager class when an SMS has been sent
 */
public class SmsManagerInputPort extends IntentService {

    private static final String ACTION_MESSAGE_SENT = "message_send";
    private static final String ACTION_MESSAGE_FAILED_SEND = "failed_send";
    private static final String EXTRA_MESSAGE = "message_id";
    private static final String EXTRA_RESENT = "resent";
    private final MessageRepository messageRepository;
    private final EventBus eventBus;

    public SmsManagerInputPort() {
        super("SmsSendNotificationService");
        setIntentRedelivery(true);
        SmsDatabaseWriter smsDatabaseWriter = new SmsDatabaseWriter(this);
        eventBus = new BroadcastEventBus(this);
        messageRepository = new MessageRepository(smsDatabaseWriter);
    }

    static Intent sentIntent(Context context, SmsMessage message) {
        return getIntent(context, message, ACTION_MESSAGE_SENT);
    }

    private static Intent resentIntent(Context context, SmsMessage message) {
        return getIntent(context, message, ACTION_MESSAGE_SENT)
                .putExtra(EXTRA_RESENT, true);
    }

    public static Intent failedSentIntent(Context context, SmsMessage message) {
        return getIntent(context, message, ACTION_MESSAGE_FAILED_SEND);
    }

    private static Intent getIntent(Context context, SmsMessage message, String action) {
        Intent sentIntent = new Intent(context, SmsManagerInputPort.class);
        sentIntent.setAction(action);
        sentIntent.putExtra(EXTRA_MESSAGE, message);
        return sentIntent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SmsMessage message = (SmsMessage) intent.getSerializableExtra(EXTRA_MESSAGE);
        if (ACTION_MESSAGE_SENT.equals(intent.getAction())) {
            messageRepository.sent(message.getId());
            SingletonManager.getMessageTransport(this).sentFromThread(message.changeTypeTo(SmsMessage.Type.SENT));
            eventBus.postMessageSent(message.getAddress());
            if (intent.getBooleanExtra(EXTRA_RESENT, false)) {
                SingletonManager.getNotifier(this).showResentMessage(message.getId());
            }
        } else if (ACTION_MESSAGE_FAILED_SEND.equals(intent.getAction())) {
            notifyFailureToSend(message);
            messageRepository.failedToSend(message.getId());
        }
    }

    private void notifyFailureToSend(final SmsMessage message) {
        SingletonManager.getMessagesLoader(this).queryContact(message.getAddress(), new OnContactQueryListener() {
            @Override
            public void contactLoaded(Contact contact) {
                SingletonManager.getNotifier(SmsManagerInputPort.this).showSendError(message, contact);
            }
        });
    }

    public static final class InputReceiver extends BroadcastReceiver {

        private static final String EXTRA_MESSAGE_ID = "message_id";
        private static final String EXTRA_PHONE_NUMBER = "phone_number";

        static PendingIntent broadcast(Context context, SmsMessage message) {
            Intent intent = new Intent(context, InputReceiver.class);
            intent.putExtra(SmsManagerInputPort.EXTRA_MESSAGE, message);
            return PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        static PendingIntent broadcastResent(Context context, SmsMessage message) {
            Intent intent = new Intent(context, InputReceiver.class);
            intent.putExtra(SmsManagerInputPort.EXTRA_MESSAGE, message);
            intent.putExtra(SmsManagerInputPort.EXTRA_RESENT, true);
            return PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            SmsMessage message = (SmsMessage) intent.getSerializableExtra(EXTRA_MESSAGE);
            if (sentSuccessfully()) {
                if (isResent(intent)) {
                    context.startService(SmsManagerInputPort.resentIntent(context, message));
                } else {
                    context.startService(SmsManagerInputPort.sentIntent(context, message));
                }
            } else {
                context.startService(SmsManagerInputPort.failedSentIntent(context, message));
            }
        }

        private boolean isResent(Intent intent) {
            return intent.getBooleanExtra(EXTRA_RESENT, false);
        }

        private boolean sentSuccessfully() {
            return getResultCode() == Activity.RESULT_OK;
        }

    }

}

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
import android.os.Parcelable;

import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.data.ParcelablePhoneNumber;
import com.amlcurran.messages.events.BroadcastEventBus;

public class SmsSentNotificationService extends IntentService {

    private static final String ACTION_MESSAGE_SENT = "message_send";
    private static final String ACTION_MESSAGE_FAILED_SEND = "failed_send";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_OUTBOX_URI = "outbox_uri";
    private static final String EXTRA_MESSAGE_ID = "message_id";
    private static final String EXTRA_PHONE_NUMBER = "phone_number";
    private final MessageRepository messageRepository;
    private final EventBus eventBus;

    public SmsSentNotificationService() {
        super("SmsSendNotificationService");
        setIntentRedelivery(true);
        SmsDatabaseWriter smsDatabaseWriter = new SmsDatabaseWriter(this);
        eventBus = new BroadcastEventBus(this);
        messageRepository = new MessageRepository(smsDatabaseWriter);
    }

    static Intent sentIntent(Context context, long messageId, PhoneNumber phoneNumber) {
        return getIntent(context, messageId, ACTION_MESSAGE_SENT, phoneNumber);
    }

    public static Intent failedSentIntent(Context context, long messageId, PhoneNumber phoneNumber) {
        return getIntent(context, messageId, ACTION_MESSAGE_FAILED_SEND, phoneNumber);
    }

    private static Intent getIntent(Context context, long messageId, String action, PhoneNumber phoneNumber) {
        Intent sentIntent = new Intent(context, SmsSentNotificationService.class);
        sentIntent.setAction(action);
        sentIntent.putExtra(EXTRA_PHONE_NUMBER, (Parcelable) phoneNumber);
        sentIntent.putExtra(EXTRA_MESSAGE_ID, messageId);
        return sentIntent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long messageId = intent.getLongExtra(EXTRA_MESSAGE_ID, -1);
        PhoneNumber phoneNumber = (ParcelablePhoneNumber) intent.getParcelableExtra(EXTRA_PHONE_NUMBER);
        if (ACTION_MESSAGE_SENT.equals(intent.getAction())) {
            messageRepository.sent(messageId);
            eventBus.postMessageSent(phoneNumber);
        } else if (ACTION_MESSAGE_FAILED_SEND.equals(intent.getAction())) {
            notifyFailureToSend();
            eventBus.postMessageDrafted(phoneNumber);
            messageRepository.failedToSend(messageId);
        }
    }

    private void notifyFailureToSend() {
//        SingletonManager.getMessagesLoader(this).queryContact(message.getPhoneNumber(), new OnContactQueryListener() {
//            @Override
//            public void contactLoaded(Contact contact) {
//                SingletonManager.getNotifier(SmsSentNotificationService.this).showSendError("person", contact);
//            }
//        });
    }
}

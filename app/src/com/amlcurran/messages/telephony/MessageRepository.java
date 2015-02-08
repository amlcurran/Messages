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
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.data.InFlightSmsMessage;

public class MessageRepository {
    private final SmsDatabaseWriter smsDatabaseWriter;
    private final EventBus eventBus;

    public MessageRepository(SmsDatabaseWriter smsDatabaseWriter, EventBus eventBus) {
        this.smsDatabaseWriter = smsDatabaseWriter;
        this.eventBus = eventBus;
    }

    void failedToSend(InFlightSmsMessage message, Uri outboxSms) {
        smsDatabaseWriter.changeSmsToType(outboxSms, SmsMessage.Type.FAILED);
        eventBus.postMessageDrafted(message.getPhoneNumber());
    }

    void sent(InFlightSmsMessage message, Uri outboxSms) {
        smsDatabaseWriter.changeSmsToType(outboxSms, SmsMessage.Type.SENT);
        eventBus.postMessageSent(message.getPhoneNumber());
    }

    boolean successfullySent(Intent intent) {
        return intent.getIntExtra(SmsReceiver.EXTRA_RESULT, 0) == Activity.RESULT_OK;
    }

    Uri send(InFlightSmsMessage message, ContentResolver contentResolver) {
        return smsDatabaseWriter.writeOutboxSms(contentResolver, message);
    }
}
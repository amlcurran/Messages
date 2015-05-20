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

import android.content.ContentResolver;
import android.net.Uri;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.InFlightSmsMessageFactory;

public class MessageRepository {
    private final SmsDatabaseWriter smsDatabaseWriter;

    public MessageRepository(SmsDatabaseWriter smsDatabaseWriter) {
        this.smsDatabaseWriter = smsDatabaseWriter;
    }

    void failedToSend(long id) {
        smsDatabaseWriter.edit(id)
                .changeSmsToType(SmsMessage.Type.FAILED)
                .updateTime()
                .commit();
    }

    void sent(long id) {
        smsDatabaseWriter.edit(id)
                .changeSmsToType(SmsMessage.Type.SENT)
                .updateTime()
                .commit();
    }

    SmsMessage send(InFlightSmsMessage message, ContentResolver contentResolver) {
        return smsDatabaseWriter.writeOutboxSms(contentResolver, message);
    }

    public SmsMessage resend(SmsMessage message, ContentResolver contentResolver) {
        Uri messageUri = smsDatabaseWriter.edit(message.getId())
                .changeSmsToType(SmsMessage.Type.SENDING)
                .updateTime().commit();
        return InFlightSmsMessageFactory.fromUri(messageUri, contentResolver);
    }
}

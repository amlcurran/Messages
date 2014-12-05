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

package com.amlcurran.messages.loaders;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.Telephony;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.data.InFlightSmsMessageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class UnreadMessagesTask implements Callable {
    private final ContentResolver resolver;
    private final ThreadListener threadListener;
    private final Handler uiHandler;

    public UnreadMessagesTask(ContentResolver resolver, ThreadListener threadListener, Handler uiHandler) {
        this.resolver = resolver;
        this.threadListener = threadListener;
        this.uiHandler = uiHandler;
    }

    @Override
    public Object call() throws Exception {
        Cursor cursor = resolver.query(Telephony.Sms.CONTENT_URI, null, Telephony.Sms.READ + "=0", null, null);
        final List<SmsMessage> messages = new ArrayList<SmsMessage>();

        SmsMessage tempPointer;
        while (cursor.moveToNext()) {
            tempPointer = InFlightSmsMessageFactory.fromCursor(cursor);
            if (tempPointer != null) {
                messages.add(tempPointer);
            }
        }

        cursor.close();
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                threadListener.onThreadLoaded(messages);
            }
        });
        return null;
    }
}

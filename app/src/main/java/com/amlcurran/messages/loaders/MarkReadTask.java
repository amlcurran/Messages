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
import android.content.ContentValues;
import android.provider.Telephony;

import com.amlcurran.messages.events.EventBus;

import java.util.concurrent.Callable;

class MarkReadTask implements Callable<Object> {
    private final ContentResolver contentResolver;
    private final String threadId;
    private final EventBus eventBus;

    public MarkReadTask(ContentResolver contentResolver, String threadId, EventBus eventBus) {
        this.contentResolver = contentResolver;
        this.threadId = threadId;
        this.eventBus = eventBus;
    }

    @Override
    public Object call() throws Exception {
        String selection = String.format("%1$s=? AND %2$s=?", Telephony.Sms.THREAD_ID, Telephony.Sms.READ);
        String[] args = new String[]{threadId, "0" };
        contentResolver.update(Telephony.Sms.CONTENT_URI, createReadContentValues(), selection, args);
        eventBus.postListInvalidated();
        return null;
    }

    private ContentValues createReadContentValues() {
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.READ, "1");
        return values;
    }

}

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

import com.amlcurran.messages.core.events.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class MarkReadTask implements Callable<Object> {
    private final ContentResolver contentResolver;
    private final List<String> threadIds;
    private final EventBus eventBus;

    public MarkReadTask(ContentResolver contentResolver, EventBus eventBus, List<String> threadIds) {
        this.contentResolver = contentResolver;
        this.threadIds = new ArrayList<String>(threadIds);
        this.eventBus = eventBus;
    }

    @Override
    public Object call() throws Exception {
        for (String threadId : threadIds) {
            String selection = String.format("%1$s=? AND (%2$s=? OR %3$s=?)", Telephony.Sms.THREAD_ID, Telephony.Sms.READ, Telephony.Sms.SEEN);
            String[] args = new String[]{threadId, "0", "0"};
            contentResolver.update(Telephony.Sms.CONTENT_URI, createReadContentValues(), selection, args);
        }
        eventBus.postListInvalidated();
        return null;
    }

    private ContentValues createReadContentValues() {
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.READ, "1");
        values.put(Telephony.Sms.SEEN, "1");
        return values;
    }

}

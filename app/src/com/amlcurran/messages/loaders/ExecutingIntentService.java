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

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.events.BroadcastEventBus;

/**
 * Service which will handle tasks asynchronously, if there's no callback requirement
 * (e.g. from Notifications)
 */
public class ExecutingIntentService extends IntentService {

    private static final String BASE_ACTION = ExecutingIntentService.class.getCanonicalName() + ".";
    private static final String EXECUTE_MARK_UNREAD = BASE_ACTION + "mark_unread";
    private static final String EXTRA_THREAD_ID = "threadId";

    public ExecutingIntentService() {
        super("ExecutingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (EXECUTE_MARK_UNREAD.equals(intent.getAction())) {
            String threadId = intent.getStringExtra(EXTRA_THREAD_ID);
            try {
                new MarkReadTask(getContentResolver(), threadId, new BroadcastEventBus(this)).call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Intent markReadIntent(Context context, Conversation conversation) {
        Intent intent = new Intent(context, ExecutingIntentService.class);
        intent.setAction(EXECUTE_MARK_UNREAD);
        intent.putExtra(EXTRA_THREAD_ID, conversation.getThreadId());
        return intent;
    }

    public static PendingIntent markReadPendingIntent(Context context, Conversation conversation) {
        return PendingIntent.getService(context, 0, markReadIntent(context, conversation), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

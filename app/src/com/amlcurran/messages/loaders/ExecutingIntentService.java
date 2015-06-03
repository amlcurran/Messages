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

import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.conversationlist.Conversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service which will handle tasks asynchronously, if there's no callback requirement
 * (e.g. from Notifications)
 */
public class ExecutingIntentService extends IntentService {

    private static final String BASE_ACTION = ExecutingIntentService.class.getCanonicalName() + ".";
    private static final String EXECUTE_MARK_READ = BASE_ACTION + "mark_read";
    private static final String EXTRA_THREAD_ID_LIST = "threadId";

    public ExecutingIntentService() {
        super("ExecutingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (EXECUTE_MARK_READ.equals(intent.getAction())) {
            SingletonManager.getNotifier(this).clearNewMessagesNotification();
            List<String> threadIds = intent.getStringArrayListExtra(EXTRA_THREAD_ID_LIST);
            try {
                new MarkReadTask(getContentResolver(), SingletonManager.getConversationList(this), threadIds).call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Intent markReadIntent(Context context, List<String> threadIdList) {
        Intent intent = new Intent(context, ExecutingIntentService.class);
        intent.setAction(EXECUTE_MARK_READ);
        intent.putExtra(EXTRA_THREAD_ID_LIST, new ArrayList<>(threadIdList));
        return intent;
    }

    private static ArrayList<String> buildThreadIdList(List<Conversation> conversations) {
        ArrayList<String> threadIds = new ArrayList<String>();
        for (Conversation conversation : conversations) {
            threadIds.add(conversation.getThreadId());
        }
        return threadIds;
    }

    public static PendingIntent markReadPendingIntent(Context context, List<Conversation> conversations) {
        return PendingIntent.getService(context, 0, markReadIntent(context, buildThreadIdList(conversations)), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent markReadPendingIntentSingle(Context context, String threadId) {
        return PendingIntent.getService(context, threadId.hashCode(), markReadIntent(context, Collections.singletonList(threadId)), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

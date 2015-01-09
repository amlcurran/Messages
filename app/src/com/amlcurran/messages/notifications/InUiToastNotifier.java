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

package com.amlcurran.messages.notifications;

import android.content.Context;
import android.widget.Toast;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.conversationlist.Conversation;

import java.util.List;

public class InUiToastNotifier implements InUiNotifier {
    private final Context context;

    public InUiToastNotifier(Context context) {
        this.context = context;
    }

    @Override
    public void notify(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deletedConversations(List<Conversation> conversations) {
        String toast;
        if (conversations.size() == 1) {
            toast = context.getString(R.string.deleted_one_thread, conversations.get(0).getContact().getDisplayName());
        } else {
            toast = context.getString(R.string.deleted_many_threads, conversations.size());
        }
        notify(toast);
    }
}
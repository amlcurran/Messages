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

package com.amlcurran.messages;

import android.content.Context;

import com.amlcurran.messages.core.conversationlist.ConversationList;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.core.conversationlist.ConversationLoader;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.core.threads.MessageTransport;
import com.amlcurran.messages.loaders.photos.PhotoLoader;
import com.amlcurran.messages.notifications.Notifier;
import com.amlcurran.messages.reporting.StatReporter;

public class SingletonManager {

    private static MessagesApp getMessagesApp(Context context) {
        return (MessagesApp) context.getApplicationContext();
    }

    public static EventBus getEventBus(Context context) {
        return getMessagesApp(context).eventBus;
    }

    public static MessagesLoader getMessagesLoader(Context context) {
        return getMessagesApp(context).loader;
    }

    public static Notifier getNotifier(Context context) {
        return getMessagesApp(context).notifier;
    }

    public static ConversationList getConversationList(Context context) {
        return getMessagesApp(context).conversationList;
    }

    public static PhotoLoader getPhotoLoader(Context context) {
        return getMessagesApp(context).photoLoader;
    }

    public static ConversationLoader getConversationLoader(Context context) {
        return getMessagesApp(context).conversationLoader;
    }

    public static StatReporter getStatReporter(Context context) {
        return getMessagesApp(context).stats;
    }

    public static MessageTransport getMessageTransport(Context context) {
        return getMessagesApp(context).messageTransport;
    }

}

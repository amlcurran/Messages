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

import com.amlcurran.messages.events.EventBus;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.reporting.StatReporter;

public class SingletonManager {

    private static MessagesApp getMessagesApp(Context context) {
        return (MessagesApp) context.getApplicationContext();
    }

    public static StatReporter getStatsReporter(Context context) {
        return getMessagesApp(context).statsReporter;
    }

    public static EventBus getEventBus(Context context) {
        return getMessagesApp(context).eventBus;
    }

    public static MessagesLoader getMessagesLoader(Context context) {
        return getMessagesApp(context).loader;
    }

    public static com.amlcurran.messages.notifications.Notifier getNotifier(Context context) {
        return getMessagesApp(context).notifier;
    }
}

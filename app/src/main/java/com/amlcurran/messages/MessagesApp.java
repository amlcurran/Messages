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

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;

import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.events.BroadcastEventSubscriber;
import com.amlcurran.messages.events.EventBus;
import com.amlcurran.messages.loaders.ExecutorMessagesLoader;
import com.amlcurran.messages.loaders.MemoryMessagesCache;
import com.amlcurran.messages.loaders.MessagesCache;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.notifications.Notifier;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagesApp extends Application implements BroadcastEventSubscriber.Listener {

    private MessagesLoader loader;
    private Notifier notifier;
    private BroadcastEventSubscriber subscriber;
    private MessagesCache cache;
    private EventBus eventBus;

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        ExecutorService executor = Executors.newCachedThreadPool();
        cache = new MemoryMessagesCache();
        notifier = new Notifier(this);
        eventBus = new BroadcastEventBus(this);
        loader = new ExecutorMessagesLoader(this, executor, cache, eventBus);
        subscriber = new BroadcastEventSubscriber(this, this);
        subscriber.startListening(BroadcastEventBus.BROADCAST_LIST_INVALIDATED);
        primeZygote(executor);
    }

    private void primeZygote(ExecutorService executor) {
        executor.submit(new PrimePreferencesTask());
        executor.submit(new PrimeLinkifyTask());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        subscriber.stopListening();
        loader.cancelAll();
    }

    public static MessagesLoader getMessagesLoader(Context context) {
        return ((MessagesApp) context.getApplicationContext()).loader;
    }

    public static Notifier getNotifier(Context context) {
        return ((MessagesApp) context.getApplicationContext()).notifier;
    }

    @Override
    public void onMessageReceived() {
        cache.invalidate();
        loader.loadConversationList(null, new PreferenceStore(this).getConversationSort());
    }

    private class PrimeLinkifyTask implements Callable<Object> {

        @Override
        public Object call() throws Exception {
            SpannableStringBuilder builder = new SpannableStringBuilder("911");
            Linkify.addLinks(builder, Linkify.ALL);
            return null;
        }

    }

    private class PrimePreferencesTask implements Callable<Object> {
        @Override
        public Object call() throws Exception {
            PreferenceManager.getDefaultSharedPreferences(MessagesApp.this).getBoolean("test", false);
            return null;
        }
    }
}

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
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;

import com.amlcurran.messages.conversationlist.data.ExecutorConversationLoader;
import com.amlcurran.messages.core.CommandQueue;
import com.amlcurran.messages.core.Log;
import com.amlcurran.messages.core.conversationlist.ConversationList;
import com.amlcurran.messages.core.conversationlist.ConversationLoader;
import com.amlcurran.messages.core.events.Broadcast;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.core.loaders.TaskQueue;
import com.amlcurran.messages.core.threads.MessageTransport;
import com.amlcurran.messages.demo.DemoMessagesLoader;
import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.events.BroadcastEventSubscriber;
import com.amlcurran.messages.loaders.MemoryMessagesCache;
import com.amlcurran.messages.loaders.MessagesCache;
import com.amlcurran.messages.loaders.TaskQueueMessagesLoader;
import com.amlcurran.messages.loaders.photos.AndroidPhotoLoader;
import com.amlcurran.messages.loaders.photos.PhotoLoader;
import com.amlcurran.messages.notifications.Notifier;
import com.amlcurran.messages.preferences.SharedPreferenceStore;
import com.amlcurran.messages.reporting.EasyTrackerStatReporter;
import com.amlcurran.messages.reporting.LoggingStatReporter;
import com.amlcurran.messages.reporting.StatReporter;
import com.amlcurran.messages.reporting.UserPreferenceWrappingStatReporter;
import com.google.analytics.tracking.android.EasyTracker;
import com.squareup.leakcanary.LeakCanary;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagesApp extends Application implements BroadcastEventSubscriber.Listener {

    private BroadcastEventSubscriber subscriber;
    private MessagesCache cache;
    private UpdateNotificationListener updateNotificationListener;
    MessagesLoader loader;
    ConversationLoader conversationLoader;
    PhotoLoader photoLoader;
    Notifier notifier;
    EventBus eventBus;
    ConversationList conversationList;
    StatReporter stats;
    MessageTransport messageTransport;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        Log.setLogger(new NewMessagesLogger());
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        ExecutorService executor = Executors.newCachedThreadPool();
        Handler uiHandler = new Handler(getMainLooper());
        HandlerCommandQueue uiCommandQueue = new HandlerCommandQueue(uiHandler);

        stats = createStatReporter();
        cache = new MemoryMessagesCache();
        eventBus = new BroadcastEventBus(this);
        messageTransport = new AndroidMessageTransport(this);
        messageTransport.start();

        if (BuildConfig.FLAVOR.equals("demo")) {
            DemoMessagesLoader demoMessagesLoader = new DemoMessagesLoader(this);
            loader = demoMessagesLoader;
            conversationLoader = demoMessagesLoader;
        } else {
            loader = new TaskQueueMessagesLoader(this, new TaskQueue(executor), uiHandler);
            conversationLoader = new ExecutorConversationLoader(new TaskQueue(executor), this, uiCommandQueue);
        }
        photoLoader = new AndroidPhotoLoader(this, cache, new TaskQueue(Executors.newFixedThreadPool(4)), uiHandler);

        notifier = new Notifier(this);
        subscriber = new BroadcastEventSubscriber(this);
        subscriber.startListening(this,
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_SENT, null),
                new Broadcast(BroadcastEventBus.BROADCAST_MESSAGE_RECEIVED, null),
                new Broadcast(BroadcastEventBus.BROADCAST_LIST_INVALIDATED, null));

        updateNotificationListener = new UpdateNotificationListener(notifier);

        conversationList = new ConversationList(conversationLoader, new SharedPreferenceStore(this), uiCommandQueue);
        conversationList.addCallbacks(updateNotificationListener);

        primeZygote(executor);

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .build());
        }

    }

    private StatReporter createStatReporter() {
        StatReporter reporter;
        if (BuildConfig.DEBUG) {
            reporter = new LoggingStatReporter();
        } else {
            reporter = new EasyTrackerStatReporter(null, EasyTracker.getInstance(this));
        }
        return new UserPreferenceWrappingStatReporter(reporter, new SharedPreferenceStore(this));
    }

    private void primeZygote(ExecutorService executor) {
        executor.submit(new PrimePreferencesTask());
        executor.submit(new PrimeLinkifyTask());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        conversationList.removeCallbacks(updateNotificationListener);
        subscriber.stopListening();
        loader.cancelAll();
        messageTransport.stop();
    }

    @Override
    public void onMessageReceived(Broadcast broadcast) {
        cache.invalidate();
        conversationList.reloadConversations();
    }

    private static class HandlerCommandQueue implements CommandQueue {

        private final Handler uiHandler;

        public HandlerCommandQueue(Handler uiHandler) {
            this.uiHandler = uiHandler;
        }

        @Override
        public void enqueue(Runnable runnable) {
            uiHandler.post(runnable);
        }

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

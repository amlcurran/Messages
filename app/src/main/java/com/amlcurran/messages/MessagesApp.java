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

import com.amlcurran.messages.loaders.ExecutorMessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.notifications.Notifier;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagesApp extends Application {

    private MessagesLoader loader;
    private Notifier notifier;

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        ExecutorService executor = Executors.newCachedThreadPool();
        loader = new ExecutorMessagesLoader(this, executor);
        notifier = new Notifier(this);
        primeZygote(executor);
    }

    private void primeZygote(ExecutorService executor) {
        executor.submit(new PrimeLinkifyTask());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        loader.cancelAll();
    }

    public static MessagesLoader getMessagesLoader(Context context) {
        return ((MessagesApp) context.getApplicationContext()).loader;
    }

    public static Notifier getNotifier(Context context) {
        return ((MessagesApp) context.getApplicationContext()).notifier;
    }

    private class PrimeLinkifyTask implements Callable<Object> {

        @Override
        public Object call() throws Exception {
            SpannableStringBuilder builder = new SpannableStringBuilder("911");
            Linkify.addLinks(builder, Linkify.ALL);
            return null;
        }

    }
}

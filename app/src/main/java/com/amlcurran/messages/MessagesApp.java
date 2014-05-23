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

import com.amlcurran.messages.loaders.ExecutorMessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoader;

import java.util.concurrent.Executors;

public class MessagesApp extends Application {

    private MessagesLoader loader;

    @Override
    public void onCreate() {
        super.onCreate();
        loader = new ExecutorMessagesLoader(this, Executors.newCachedThreadPool());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        loader.cancelAll();
    }

    public static MessagesLoader getMessagesLoader(Context context) {
        return ((MessagesApp) context.getApplicationContext()).loader;
    }

}

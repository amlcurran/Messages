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

import android.content.Context;
import android.os.Handler;

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.data.Contact;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AndroidPhotoLoader implements PhotoLoader {

    private final Context context;
    private final MessagesCache cache;
    private final ExecutorService executor;
    private final Handler uiHandler;

    public AndroidPhotoLoader(Context context, MessagesCache cache, ExecutorService executor, Handler uiHandler) {
        this.context = context;
        this.cache = cache;
        this.executor = executor;
        this.uiHandler = uiHandler;
    }

    private Task submit(final Callable task) {
        Future result = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    task.call();
                } catch (Exception e) {
                    MessagesLog.e(AndroidPhotoLoader.this, e);
                    throw new RuntimeException(e);
                }
            }
        });
        return new FutureTask(result);
    }

    @Override
    public Task loadPhoto(Contact contact, PhotoLoadListener photoLoadListener) {
        return submit(new PhotoLoadTask(context.getContentResolver(), context.getResources(), contact, photoLoadListener, cache, uiHandler));
    }
}

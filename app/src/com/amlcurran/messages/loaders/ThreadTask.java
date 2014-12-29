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

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Handler;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.loaders.ThreadListener;

import java.util.List;
import java.util.concurrent.Callable;

class ThreadTask implements Callable<Object> {

    private final ContentResolver contentResolver;
    private final String threadId;
    private final Uri contentUri;
    private final ThreadListener threadListener;
    private final Handler uiHandler;
    private final ThreadLoader threadLoader;

    public ThreadTask(ContentResolver contentResolver, String threadId, Uri contentUri, ThreadListener threadListener, Handler uiHandler) {
        this.contentResolver = contentResolver;
        this.threadId = threadId;
        this.contentUri = contentUri;
        this.threadListener = threadListener;
        this.threadLoader = new ThreadLoader(contentUri, contentResolver);
        this.uiHandler = uiHandler;
    }

    @Override
    public Object call() throws Exception {
        final List<SmsMessage> messageList = threadLoader.loadSmsList(threadId);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                threadListener.onThreadLoaded(messageList);
            }
        });
        return null;
    }

}

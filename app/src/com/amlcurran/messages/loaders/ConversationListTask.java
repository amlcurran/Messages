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

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.loaders.fudges.ConversationListHelperFactory;

import java.util.List;
import java.util.concurrent.Callable;

class ConversationListTask implements Callable<Object> {

    private final String query;
    private final String[] args;
    private final ConversationListListener loadListener;
    private final MessagesCache cache;
    private final ConversationListLoader conversationListLoader;

    ConversationListTask(ContentResolver contentResolver, String query, String[] args, ConversationListListener loadListener, Sort sort, MessagesCache cache) {
        this.query = query;
        this.args = args;
        this.loadListener = loadListener;
        this.cache = cache;
        conversationListLoader = new ConversationListLoader(contentResolver, sort, ConversationListHelperFactory.get());
    }

    public ConversationListTask(ContentResolver contentResolver, ConversationListListener loadListener, Sort sort, MessagesCache cache) {
        this(contentResolver, null, null, loadListener, sort, cache);
    }

    @Override
    public Object call() throws Exception {
        long startTime = System.currentTimeMillis();
        List<Conversation> conversations = conversationListLoader.loadList(query, args);
        cache.storeConversationList(conversations);
        loadListener.onConversationListLoaded(conversations);
        MessagesLog.d(this, String.format("Millis taken: %d", System.currentTimeMillis() - startTime));
        return null;
    }

}

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

package com.amlcurran.messages.conversationlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.preferences.PreferenceStore;

public class ConversationViewFactory {
    private final PreferenceStore preferenceStore;
    private final TextFormatter textFormatter;
    private final DraftRepository draftRepository;
    private final AdapterPhotoLoader adapterPhotoLoader;

    public ConversationViewFactory(PreferenceStore preferenceStore, TextFormatter textFormatter, DraftRepository draftRepository, AdapterPhotoLoader adapterPhotoLoader) {
        this.preferenceStore = preferenceStore;
        this.textFormatter = textFormatter;
        this.draftRepository = draftRepository;
        this.adapterPhotoLoader = adapterPhotoLoader;
    }

    ConversationViewHolder createUnreadViewHolder(Context context, ViewGroup parent, ConversationListView.ConversationSelectedListener conversationSelectedListener) {
        int layoutRes;
        if (preferenceStore.showLargeUnreadPreviews()) {
            layoutRes = R.layout.item_conversation_large;
        } else {
            layoutRes = R.layout.item_conversation;
        }
        View view = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        return new ConversationViewHolder(view, conversationSelectedListener, textFormatter, draftRepository, new UnreadConversationStyler(textFormatter), adapterPhotoLoader);
    }

    ConversationViewHolder createReadViewHolder(Context context, ViewGroup parent, ConversationListView.ConversationSelectedListener conversationSelectedListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view, conversationSelectedListener, textFormatter, draftRepository, new ReadConversationStyler(textFormatter), adapterPhotoLoader);
    }

}
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
import com.amlcurran.messages.core.preferences.PreferenceStore;

class ConversationViewCreator {
    private final PreferenceStore preferenceStore;

    public ConversationViewCreator(PreferenceStore preferenceStore) {
        this.preferenceStore = preferenceStore;
    }

    View createUnreadView(Context context, ViewGroup parent, ConversationListView.ConversationSelectedListener conversationController) {
        int layoutRes;
        if (preferenceStore.showLargeUnreadPreviews()) {
            layoutRes = R.layout.item_conversation_unread_large;
        } else {
            layoutRes = R.layout.item_conversation_unread;
        }
        View view = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        view.setTag(R.id.tag_view_holder, new ConversationViewHolder(view, conversationController));
        return view;
    }

    ConversationViewHolder createUnreadViewHolder(Context context, ViewGroup parent, ConversationListView.ConversationSelectedListener conversationSelectedListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation_read, parent, false);
        return new ConversationViewHolder(view, conversationSelectedListener);
    }

    View createReadView(Context context, ViewGroup parent, ConversationListView.ConversationSelectedListener conversationController) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation_read, parent, false);
        view.setTag(R.id.tag_view_holder, new ConversationViewHolder(view, conversationController));
        return view;
    }

    ConversationViewHolder createReadViewHolder(Context context, ViewGroup parent, ConversationListView.ConversationSelectedListener conversationSelectedListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation_read, parent, false);
        return new ConversationViewHolder(view, conversationSelectedListener);
    }

}
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

import android.view.ViewGroup;

import com.amlcurran.messages.conversationlist.SelectionStateHolder;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.github.amlcurran.sourcebinder.recyclerview.ViewHolderBinder;

public class ConversationsRecyclerBinder implements ViewHolderBinder<Conversation, ConversationViewHolder> {

    private static final int IS_UNREAD = 1;
    private static final int IS_READ = 0;
    private final ConversationListView.ConversationSelectedListener conversationSelectedListener;
    private final ConversationViewFactory viewCreator;
    private final AdapterPhotoLoader adapterPhotoLoader;
    private final SelectionStateHolder checkedStateProvider;

    public ConversationsRecyclerBinder(ConversationListView.ConversationSelectedListener conversationSelectedListener,
                                       SelectionStateHolder checkedStateProvider,
                                       ConversationViewFactory conversationViewFactory,
                                       AdapterPhotoLoader adapterPhotoLoader) {
        this.conversationSelectedListener = conversationSelectedListener;
        this.checkedStateProvider = checkedStateProvider;
        this.viewCreator = conversationViewFactory;
        this.adapterPhotoLoader = adapterPhotoLoader;
    }

    @Override
    public ConversationViewHolder createViewHolder(ViewGroup viewGroup, int i) {
        if (i == IS_UNREAD) {
            return viewCreator.createUnreadViewHolder(viewGroup.getContext(), viewGroup, conversationSelectedListener);
        } else {
            return viewCreator.createReadViewHolder(viewGroup.getContext(), viewGroup, conversationSelectedListener);
        }
    }

    @Override
    public void bindViewHolder(ConversationViewHolder viewHolder, Conversation item) {
        adapterPhotoLoader.stopLoadingPhoto(viewHolder);
        adapterPhotoLoader.loadContactPhoto(viewHolder, item);

        viewHolder.getView().setActivated(checkedStateProvider.isChecked(item));
        viewHolder.getImageView().setActivated(checkedStateProvider.isChecked(item));
        viewHolder.bind(item);
    }

    @Override
    public int getItemViewHolderType(int i, Conversation conversation) {
        return conversation.isRead() ? IS_READ : IS_UNREAD;
    }
}

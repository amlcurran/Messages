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

import android.content.res.Resources;
import android.view.ViewGroup;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.loaders.photos.PhotoLoader;
import com.github.amlcurran.sourcebinder.recyclerview.ViewHolderBinder;

public class ConversationsRecyclerBinder implements ViewHolderBinder<Conversation, ConversationViewHolder> {

    private static final int IS_UNREAD = 1;
    private static final int IS_READ = 0;
    private final DraftRepository draftRepository;
    private final String draftPreamble;
    private final String fromMePreamble;
    private final TextFormatter textFormatter;
    private final ConversationListView.ConversationSelectedListener conversationSelectedListener;
    private final ConversationViewCreator viewCreator;
    private final AdapterPhotoLoader adapterPhotoLoader;

    public ConversationsRecyclerBinder(DraftRepository draftRepository, Resources resources, PhotoLoader loader, TextFormatter textFormatter, ConversationListView.ConversationSelectedListener conversationSelectedListener) {
        this.draftRepository = draftRepository;
        this.textFormatter = textFormatter;
        this.conversationSelectedListener = conversationSelectedListener;
        this.draftPreamble = resources.getString(R.string.draft_preamble);
        this.fromMePreamble = resources.getString(R.string.from_me_preamble);
        this.viewCreator = new ConversationViewCreator(null);
        this.adapterPhotoLoader = new AdapterPhotoLoader(loader, resources);
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

        viewHolder.nameField.setText(item.getContact().getDisplayName());
        viewHolder.snippetField.setText(getSummaryText(item));
    }

    private CharSequence getSummaryText(Conversation item) {
        if (showDraftAsSummary(item)) {
            return textFormatter.constructSummary(draftPreamble, draftRepository.getDraft(item.getAddress()));
        } else if (showAsFromMe(item)) {
            return textFormatter.constructSummary(fromMePreamble, item.getSummaryText());
        }
        return item.getSummaryText();
    }

    private boolean showAsFromMe(Conversation item) {
        return item.isLastFromMe();
    }

    private boolean showDraftAsSummary(Conversation item) {
        return draftRepository.hasDraft(item.getAddress()) && item.isRead();
    }

    @Override
    public int getItemViewHolderType(int i, Conversation conversation) {
        return conversation.isRead() ? IS_READ : IS_UNREAD;
    }
}

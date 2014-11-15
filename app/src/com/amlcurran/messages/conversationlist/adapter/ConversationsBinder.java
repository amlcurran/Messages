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
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.github.amlcurran.sourcebinder.SimpleBinder;

public class ConversationsBinder extends SimpleBinder<Conversation> {

    private static final int IS_UNREAD = 1;
    private static final int IS_READ = 0;
    private final DraftRepository draftRepository;
    private final String draftPreamble;
    private final String fromMePreamble;
    private final TextFormatter textFormatter;
    private final ConversationViewCreator viewCreator;
    private final AdapterPhotoLoader adapterPhotoLoader;

    public ConversationsBinder(TextFormatter textFormatter, Resources resources, MessagesLoader loader, DraftRepository draftRepository) {
        this.draftRepository = draftRepository;
        this.draftPreamble = resources.getString(R.string.draft_preamble);
        this.fromMePreamble = resources.getString(R.string.from_me_preamble);
        this.textFormatter = textFormatter;
        this.viewCreator = new ConversationViewCreator();
        this.adapterPhotoLoader = new AdapterPhotoLoader(loader, resources.getDimension(R.dimen.photo_animation_length));
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position, Conversation item) {
        return item.isRead() ? IS_READ : IS_UNREAD;
    }

    @Override
    public View bindView(View convertView, Conversation item, int position) {
        ConversationViewHolder viewHolder = (ConversationViewHolder) convertView.getTag(R.id.tag_view_holder);

        adapterPhotoLoader.stopLoadingPhoto(viewHolder);
        adapterPhotoLoader.loadContactPhoto(viewHolder, item);

        viewHolder.nameField.setText(item.getContact().getDisplayName());
        viewHolder.snippetField.setText(getSummaryText(item));

        return convertView;
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
    public View createView(Context context, int itemViewType, ViewGroup parent) {
        if (itemViewType == IS_READ) {
            return viewCreator.createReadView(context, parent);
        } else {
            return viewCreator.createUnreadView(context, parent);
        }
    }

}

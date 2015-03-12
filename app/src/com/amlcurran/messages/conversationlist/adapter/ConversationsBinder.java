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
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;

import com.amlcurran.messages.R;
import com.amlcurran.messages.bucket.Truss;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.loaders.photos.PhotoLoader;
import com.github.amlcurran.sourcebinder.SimpleBinder;

public class ConversationsBinder extends SimpleBinder<Conversation> {

    private static final int IS_UNREAD = 1;
    private static final int IS_READ = 0;
    private final DraftRepository draftRepository;
    private final PreferenceStore preferenceStore;
    private final ConversationListView.ConversationSelectedListener conversationSelectedListener;
    private final String draftPreamble;
    private final String fromMePreamble;
    private final TextFormatter textFormatter;
    private final ConversationViewCreator viewCreator;
    private final AdapterPhotoLoader adapterPhotoLoader;
    private final Context context;

    public ConversationsBinder(Context context, TextFormatter textFormatter, Resources resources, PhotoLoader loader, DraftRepository draftRepository, PreferenceStore preferenceStore, ConversationListView.ConversationSelectedListener conversationSelectedListener) {
        this.context = context;
        this.draftRepository = draftRepository;
        this.preferenceStore = preferenceStore;
        this.conversationSelectedListener = conversationSelectedListener;
        this.draftPreamble = resources.getString(R.string.draft_preamble);
        this.fromMePreamble = resources.getString(R.string.from_me_preamble);
        this.textFormatter = textFormatter;
        this.viewCreator = new ConversationViewCreator(preferenceStore);
        this.adapterPhotoLoader = new AdapterPhotoLoader(loader, resources);
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

        viewHolder.getImageView().setTag(R.id.tag_position, position);
        viewHolder.getNameField().setText(formatTopLine(item));
        viewHolder.getSnippetField().setText(getSummaryText(item));

        return convertView;
    }

    private CharSequence formatTopLine(Conversation item) {
        Truss truss = new Truss();
        truss.append(item.getContact().getDisplayName());
        if (preferenceStore.showConversationCount()) {
            truss.append(" ")
                    .pushSpan(new TextAppearanceSpan(context, R.style.ConversationCount))
                    .append(item.getConversationCount())
                    .popSpan();
        }
        return truss.build();
    }

    private CharSequence getSummaryText(Conversation item) {
        if (showDraftAsSummary(item)) {
            return textFormatter.draftSummary(draftRepository.getDraft(item.getAddress()));
        } else if (showAsFromMe(item)) {
            return textFormatter.fromMeSummary(item.getSummaryText());
        }
        return textFormatter.fromOtherSummary(item);
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
            return viewCreator.createReadView(context, parent, conversationSelectedListener);
        } else {
            return viewCreator.createUnreadView(context, parent, conversationSelectedListener);
        }
    }

}

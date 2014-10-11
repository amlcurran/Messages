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

package com.amlcurran.messages.conversationlist;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.Task;
import com.github.amlcurran.sourcebinder.SimpleBinder;

class ConversationsBinder extends SimpleBinder<Conversation> {

    private static final int IS_UNREAD = 1;
    private static final int IS_READ = 0;
    private final float animationLength;
    private final MessagesLoader loader;
    private final DraftRepository draftRepository;
    private final String draftPreamble;
    private final String fromMePreamble;
    private final TextFormatter textFormatter;

    public ConversationsBinder(TextFormatter textFormatter, Resources resources, MessagesLoader loader, DraftRepository draftRepository) {
        this.loader = loader;
        this.draftRepository = draftRepository;
        this.animationLength = resources.getDimension(R.dimen.photo_animation_length);
        this.draftPreamble = resources.getString(R.string.draft_preamble);
        this.fromMePreamble = resources.getString(R.string.from_me_preamble);
        this.textFormatter = textFormatter;
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

        stopLoadingCurrent(convertView);
        loadContactPhoto(convertView, item, viewHolder.imageView);

        viewHolder.nameField.setText(item.getContact().getDisplayName());
        viewHolder.snippetField.setText(getSummaryText(item));

        return convertView;
    }

    private void stopLoadingCurrent(View convertView) {
        if (convertView.getTag(R.id.tag_load_task) != null) {
            Task task = (Task) convertView.getTag(R.id.tag_load_task);
            task.cancel();
        }
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

    private void loadContactPhoto(View convertView, final Conversation item, final ImageView imageView) {
        Contact contact = item.getContact();
        Task task = loader.loadPhoto(contact, new SettingPhotoLoadListener(imageView, animationLength));
        convertView.setTag(R.id.tag_load_task, task);
    }

    @Override
    public View createView(Context context, int itemViewType, ViewGroup parent) {
        if (itemViewType == IS_READ) {
            return createReadView(context, parent);
        } else {
            return createUnreadView(context, parent);
        }
    }

    private View createUnreadView(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation_unread, parent, false);
        view.setTag(R.id.tag_view_holder, new ConversationViewHolder(view));
        return view;
    }

    private View createReadView(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation_read, parent, false);
        view.setTag(R.id.tag_view_holder , new ConversationViewHolder(view));
        return view;
    }

    private static class ConversationViewHolder {

        public final TextView nameField;
        public final TextView snippetField;
        public final ImageView imageView;

        public ConversationViewHolder(View view) {
            nameField = ((TextView) view.findViewById(android.R.id.text1));
            snippetField = ((TextView) view.findViewById(android.R.id.text2));
            imageView = ((ImageView) view.findViewById(R.id.image));
        }

    }

}

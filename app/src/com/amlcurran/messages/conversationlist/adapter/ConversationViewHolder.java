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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.loaders.Task;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    private final TextView nameField;
    private final TextView snippetField;
    private final ImageView imageView;
    private final TextFormatter textFormatter;
    private Task imageTask;

    public ConversationViewHolder(final View view, final ConversationListView.ConversationSelectedListener clickCallback, TextFormatter textFormatter) {
        super(view);
        this.textFormatter = textFormatter;
        nameField = ((TextView) view.findViewById(android.R.id.text1));
        snippetField = ((TextView) view.findViewById(android.R.id.text2));
        imageView = ((ImageView) view.findViewById(R.id.image));
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(imageView)) {
                    clickCallback.secondarySelected(getPosition());
                } else if (v.equals(view)) {
                    clickCallback.selectedPosition(getPosition());
                }
            }
        };
        imageView.setOnClickListener(l);
        view.setOnClickListener(l);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Task getImageTask() {
        return imageTask;
    }

    public void setImageTask(Task imageTask) {
        this.imageTask = imageTask;
    }

    public View getView() {
        return itemView;
    }

    public void bind(Conversation item, DraftRepository draftRepository) {
        nameField.setText(item.getContact().getDisplayName());
        snippetField.setText(getSummaryText(item, draftRepository));
    }

    private CharSequence getSummaryText(Conversation item, DraftRepository draftRepository) {
        if (showDraftAsSummary(draftRepository, item)) {
            return textFormatter.draftSummary(draftRepository.getDraft(item.getAddress()));
        } else if (showAsFromMe(item)) {
            return textFormatter.fromMeSummary(item);
        }
        return textFormatter.fromOtherSummary(item);
    }

    private boolean showAsFromMe(Conversation item) {
        return item.isLastFromMe();
    }

    private boolean showDraftAsSummary(DraftRepository draftRepository, Conversation item) {
        return draftRepository.hasDraft(item.getAddress()) && item.isRead();
    }
}

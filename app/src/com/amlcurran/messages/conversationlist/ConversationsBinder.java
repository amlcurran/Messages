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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.bucket.Truss;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.Task;
import com.github.amlcurran.sourcebinder.SimpleBinder;

public class ConversationsBinder extends SimpleBinder<Conversation> {

    private static final int IS_UNREAD = 1;
    private static final int IS_READ = 0;
    private final float animationLength;
    private final Activity activity;
    private final MessagesLoader loader;
    private final DraftRepository draftRepository;
    private final String draftPreamble;
    private final int draftPreambleTextColor;
    private final String fromMePreamble;

    public ConversationsBinder(Activity activity, Resources resources, MessagesLoader loader, DraftRepository draftRepository) {
        this.activity = activity;
        this.loader = loader;
        this.draftRepository = draftRepository;
        this.animationLength = resources.getDimension(R.dimen.photo_animation_length);
        this.draftPreamble = resources.getString(R.string.draft_preamble);
        this.fromMePreamble = resources.getString(R.string.from_me_preamble);
        this.draftPreambleTextColor = resources.getColor(R.color.theme_colour);
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
            return constructSummary(draftPreamble, draftRepository.getDraft(item.getAddress()));
        } else if (showAsFromMe(item)) {
            return constructSummary(fromMePreamble, item.getSummaryText());
        }
        return item.getSummaryText();
    }

    private boolean showAsFromMe(Conversation item) {
        return item.isLastFromMe();
    }

    private boolean showDraftAsSummary(Conversation item) {
        return draftRepository.hasDraft(item.getAddress()) && item.isRead();
    }

    private CharSequence constructSummary(String preamble, String text) {
        return new Truss().pushSpan(new TextAppearanceSpan(activity, R.style.Material_Body2))
                .append(preamble)
                .popSpan()
                .append(" — ")
                .append(text)
                .build();
    }

    private void loadContactPhoto(View convertView, final Conversation item, final ImageView imageView) {
        Contact contact = item.getContact();
        Task task = loader.loadPhoto(contact, new SettingPhotoLoadListener(imageView));
        convertView.setTag(R.id.tag_load_task, task);
    }

    @Override
    public View createView(Context context, int itemViewType, ViewGroup parent) {
        if (itemViewType == IS_READ) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_conversation_read, parent, false);
            view.setTag(R.id.tag_view_holder , new ConversationViewHolder(view));
            return view;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_conversation_unread, parent, false);
            view.setTag(R.id.tag_view_holder, new ConversationViewHolder(view));
            return view;
        }
    }

    private class SettingPhotoLoadListener implements PhotoLoadListener {

        private final ImageView imageView;

        public SettingPhotoLoadListener(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void photoLoaded(final Bitmap photo) {
            imageView.setImageBitmap(photo);
            ViewPropertyAnimator propertyAnimator = imageView.animate();
            propertyAnimator
                    .translationXBy(animationLength)
                    .alpha(1f);
            imageView.setTag(propertyAnimator);
        }

        @Override
        public void photoLoadedFromCache(final Bitmap photo) {
            imageView.setImageBitmap(photo);
        }

        @Override
        public void beforePhotoLoad(Contact contact) {
            if (imageView.getTag() != null) {
                ((ViewPropertyAnimator) imageView.getTag()).cancel();
            }
            resetContactImage(imageView);
        }

        private void resetContactImage(ImageView imageView) {
            imageView.setTranslationX(-animationLength);
            imageView.setAlpha(0f);
            imageView.setImageBitmap(null);
        }

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

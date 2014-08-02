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
import android.widget.ImageView;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.bucket.Truss;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.loaders.MessagesLoader;
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
        TextView textView1 = getTextView(convertView, android.R.id.text1);
        TextView textView2 = getTextView(convertView, android.R.id.text2);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);

        if (isNotSameItem(convertView, item)) {
            loadContactPhoto(item, imageView);
        }

        textView1.setText(item.getContact().getDisplayName());
        textView2.setText(getSummaryText(item));

        convertView.setTag(item);
        return convertView;
    }

    private CharSequence getSummaryText(Conversation item) {
        if (showDraftAsSummary(item)) {
            return constructDraftSummary(item);
        } else if (showAsFromMe(item)) {
            return constructFromMeSummary(item);
        }
        return item.getSummaryText();
    }

    private boolean showAsFromMe(Conversation item) {
        return item.isLastFromMe();
    }

    private boolean showDraftAsSummary(Conversation item) {
        return draftRepository.hasDraft(item.getAddress()) && item.isRead();
    }

    private CharSequence constructFromMeSummary(Conversation item) {
        Truss truss = new Truss();
        return truss.pushSpan(new TextAppearanceSpan(activity, R.style.Material_Body2))
                .append(fromMePreamble)
                .popSpan()
                .append(" — ")
                .append(item.getSummaryText())
                .build();
    }

    private CharSequence constructDraftSummary(Conversation item) {
        Truss truss = new Truss();
        return truss.pushSpan(new TextAppearanceSpan(activity, R.style.Material_Body2))
                .append(draftPreamble)
                .popSpan()
                .append(" — ")
                .append(draftRepository.getDraft(item.getAddress()))
                .build();
    }

    private static boolean isNotSameItem(View convertView, Conversation item) {
        return convertView.getTag() != item;
    }

    private void loadContactPhoto(final Conversation item, final ImageView imageView) {
        Contact contact = item.getContact();
        loader.loadPhoto(contact, new SettingPhotoLoadListener(imageView));
    }

    private void resetContactImage(ImageView imageView) {
        imageView.setImageBitmap(null);
        imageView.setAlpha(0f);
    }

    private TextView getTextView(View convertView, int text1) {
        return (TextView) convertView.findViewById(text1);
    }

    @Override
    public View createView(Context context, int itemViewType, ViewGroup parent) {
        if (itemViewType == IS_READ) {
            return LayoutInflater.from(context).inflate(R.layout.item_conversation_read, parent, false);
        } else {
            return LayoutInflater.from(context).inflate(R.layout.item_conversation_unread, parent, false);
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
            imageView.setTranslationX(-animationLength);
            imageView.animate()
                    .translationXBy(animationLength)
                    .alpha(1f).start();
        }

        @Override
        public void photoLoadedFromCache(final Bitmap photo) {
            imageView.setImageBitmap(photo);
        }

        @Override
        public void beforePhotoLoad(Contact contact) {
            resetContactImage(imageView);
        }

    }
}

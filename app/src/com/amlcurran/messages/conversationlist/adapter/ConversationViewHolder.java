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
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.core.loaders.Task;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    public final TextView nameField;
    public final TextView snippetField;
    public final ImageView imageView;
    public Task imageTask;

    public ConversationViewHolder(final View view, final ConversationListView.ConversationSelectedListener clickCallback) {
        super(view);
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
}

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

package com.amlcurran.messages;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.amlcurran.messages.data.Conversation;

public class ConversationModalMarshall implements ActionMode.Callback {

    private final Conversation conversation;
    private final Callback callback;

    public ConversationModalMarshall(Conversation conversation, Callback callback) {
        this.conversation = conversation;
        this.callback = callback;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.modal_conversation, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {

            case R.id.modal_contact:
                callback.viewContact(conversation.getAddress());
                return true;

            case R.id.modal_delete_thread:
                callback.deleteThread(conversation);
                return true;

        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

    public interface Callback {
        void viewContact(String address);
        void deleteThread(Conversation conversation);
    }

}

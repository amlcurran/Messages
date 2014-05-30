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

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.espian.utils.ui.MenuFinder;
import com.espian.utils.data.Source;

import java.util.ArrayList;
import java.util.List;

public class ConversationModalMarshall implements AbsListView.MultiChoiceModeListener {

    private final Source<Conversation> conversationSource;
    private final Callback callback;
    private final ArrayList<Conversation> selectedConversations;

    public ConversationModalMarshall(Source<Conversation> conversationSource, Callback callback) {
        this.conversationSource = conversationSource;
        this.selectedConversations = new ArrayList<Conversation>();
        this.callback = callback;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.modal_conversation, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuFinder.findItemById(menu, R.id.modal_contact).setVisible(onlyOneSelected());
        return true;
    }

    private boolean onlyOneSelected() {
        return selectedConversations.size() == 1;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {

            case R.id.modal_contact:
                callback.viewContact(selectedConversations.get(0).getAddress());
                mode.finish();
                return true;

            case R.id.modal_delete_thread:
                callback.deleteThreads(copyConversations());
                mode.finish();
                return true;

            case R.id.modal_mark_unread:
                callback.markAsUnread(copyConversations());
                mode.finish();
                return true;

        }
        return false;
    }

    private ArrayList<Conversation> copyConversations() {
        return new ArrayList<Conversation>(selectedConversations);
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectedConversations.clear();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        Conversation checkedConversation = conversationSource.getAtPosition(position);
        if (checked) {
            selectedConversations.add(checkedConversation);
        } else {
            selectedConversations.remove(checkedConversation);
        }
        mode.invalidate();
    }

    public interface Callback {
        void viewContact(String address);
        void deleteThreads(List<Conversation> conversation);
        void markAsUnread(List<Conversation> threadId);
    }

}

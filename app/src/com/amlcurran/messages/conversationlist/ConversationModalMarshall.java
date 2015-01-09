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

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.reporting.StatReporter;
import com.amlcurran.messages.ui.contact.ContactClickListener;
import com.espian.utils.ui.MenuFinder;

import java.util.ArrayList;
import java.util.List;

public class ConversationModalMarshall implements ActionMode.Callback {

    private final ContactClickListener contactClickListener;
    private final DeleteThreadViewCallback deleteThreadsViewCallback;
    private final StatReporter statReporter;
    private final MessagesLoader messagesLoader;
    private final SelectionStateHolder<Conversation> selectionStateHolder;

    public ConversationModalMarshall(ContactClickListener contactClickListener, DeleteThreadViewCallback deleteThreadViewCallback, StatReporter statReporter, MessagesLoader messagesLoader, SelectionStateHolder<Conversation> selectionStateHolder) {
        this.contactClickListener = contactClickListener;
        this.deleteThreadsViewCallback = deleteThreadViewCallback;
        this.statReporter = statReporter;
        this.messagesLoader = messagesLoader;
        this.selectionStateHolder = selectionStateHolder;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.modal_conversation, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuFinder.findItemById(menu, R.id.modal_contact).setVisible(onlyOneSelected() && selectedSavedContact());
        MenuFinder.findItemById(menu, R.id.modal_contact_add).setVisible(onlyOneSelected() && !selectedSavedContact());
        return true;
    }

    private boolean selectedSavedContact() {
        return selectionStateHolder.firstItem().getContact().isSaved();
    }

    private boolean onlyOneSelected() {
        return selectionStateHolder.hasOneChecked();
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {

            case R.id.modal_contact:
                contactClickListener.viewContact(selectionStateHolder.firstItem().getContact());
                mode.finish();
                return true;

            case R.id.modal_delete_thread:
                deleteThreadsViewCallback.deleteThreads(copyConversations());
                mode.finish();
                return true;

            case R.id.modal_mark_unread:
                markAsUnread();
                mode.finish();
                return true;

            case R.id.modal_contact_add:
                contactClickListener.addContact(selectionStateHolder.firstItem().getContact());
                mode.finish();
                return true;

        }
        return false;
    }

    private void markAsUnread() {
        statReporter.sendUiEvent("mark_thread_unread");
        List<String> threadIds = new ArrayList<>();
        for (int i = 0; i < selectionStateHolder.checkedItemCount(); i++) {
            Conversation conversation = selectionStateHolder.itemAt(i);
            threadIds.add(conversation.getThreadId());
        }
        messagesLoader.markThreadsAsUnread(threadIds);
    }

    private ArrayList<Conversation> copyConversations() {
        return new ArrayList<>(selectionStateHolder.allItems());
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectionStateHolder.clear();
    }

}

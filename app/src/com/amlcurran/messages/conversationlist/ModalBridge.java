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
import android.widget.ListView;

import com.amlcurran.messages.MessagesLog;
import com.amlcurran.messages.core.conversationlist.ConversationListView;

public class ModalBridge implements ConversationListView.ConversationSelectedListener {
    private final ConversationModalMarshall listener;
    private final ConversationListFragment conversationListFragment;
    private final ListView listView;
    private ActionMode actionMode;

    public ModalBridge(ConversationModalMarshall listener, ConversationListFragment conversationListFragment, ListView listView) {
        this.listener = listener;
        this.conversationListFragment = conversationListFragment;
        this.listView = listView;
    }

    @Override
    public void selectedPosition(int position) {

    }

    @Override
    public void secondarySelected(int position) {
//        if (actionMode == null) {
//            actionMode = listView.startActionMode(new ActionMode.Callback() {
//                @Override
//                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                    return listener.onCreateActionMode(mode, menu);
//                }
//
//                @Override
//                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                    return listener.onPrepareActionMode(mode, menu);
//                }
//
//                @Override
//                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                    return listener.onActionItemClicked(mode, item);
//                }
//
//                @Override
//                public void onDestroyActionMode(ActionMode mode) {
//                    listener.onDestroyActionMode(mode);
//                    actionMode = null;
//                }
//            });
//        }
        //if (actionMode != null) {
            //listener.onItemCheckedStateChanged(actionMode, position, 0, listView.isItemChecked(position));
            boolean newCheckedValue = !listView.isItemChecked(position);
            MessagesLog.d(this, "new checked value for position: %1$d %2$s", position, newCheckedValue);
        listView.setItemChecked(position, newCheckedValue);
//        listener.onItemCheckedStateChanged();
            //((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            if (listView.getCheckedItemCount() == 0) {
                MessagesLog.d(this, "Checked item count is zero");
                //actionMode.finish();
            }
        //}
    }
}

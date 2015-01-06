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

import com.amlcurran.messages.core.data.Conversation;

import java.util.HashMap;
import java.util.Map;

public class ConversationSelectionStateHolder {

    private final Map<String, Boolean> checkedItems = new HashMap<>();

    public boolean isChecked(Conversation item) {
        if (checkedItems.containsKey(item.getThreadId())) {
            return checkedItems.get(item.getThreadId());
        }
        return false;
    }

    public void flipItem(Conversation item) {
        if (!checkedItems.containsKey(item.getThreadId())) {
            checkedItems.put(item.getThreadId(), true);
        } else {
            checkedItems.remove(item.getThreadId());
        }
    }
}

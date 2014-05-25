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

package com.amlcurran.messages.loaders;

import android.content.ContentResolver;
import android.provider.Telephony;

import com.amlcurran.messages.conversationlist.ConversationListListener;

class UnreadConversationListTask extends ConversationListTask {

    public UnreadConversationListTask(ContentResolver contentResolver, ConversationListListener loadListener) {
        super(contentResolver, Telephony.Sms.Inbox.READ + "=0", null, loadListener, 0);
    }
}

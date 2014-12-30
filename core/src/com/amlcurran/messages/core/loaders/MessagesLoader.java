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

package com.amlcurran.messages.core.loaders;

import com.amlcurran.messages.core.data.PhoneNumber;

import java.util.List;

public interface MessagesLoader {

    void loadThread(String threadId, ThreadListener threadListener);

    void markThreadAsRead(String threadId);

    void cancelAll();

    void queryContact(PhoneNumber phoneNumber, OnContactQueryListener onContactQueryListener);

    void markThreadsAsUnread(List<String> threadIds);

    void loadContacts(ContactListListener contactListListener);

    Task loadUnreadMessages(ThreadListener threadListener);

    interface Provider {
        MessagesLoader getMessagesLoader();
    }
}

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
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Telephony;

import com.amlcurran.messages.core.conversationlist.ConversationList;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MarkReadTaskTest {

    @Test
    public void reloadsConversationsWhenUpdated() throws Exception {
        ContentResolver mockResolver = mock(ContentResolver.class);
        when(mockResolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class)))
                .thenReturn(1);
        ConversationList mockConversations = mock(ConversationList.class);
        MarkReadTask task = new MarkReadTask(mockResolver, mockConversations, onlyOneThread(), mock(ContentValues.class));

        task.call();

        verify(mockConversations).reloadConversations();
    }
    @Test
    public void reloadsConversationsWhenOnlyOneThreadIsUpdated() throws Exception {
        ContentResolver mockResolver = mock(ContentResolver.class);
        ContentValues readContentValues = mock(ContentValues.class);
        when(mockResolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class)))
                .thenReturn(0);
        String selection = String.format("%1$s=? AND (%2$s=? OR %3$s=?)", Telephony.Sms.THREAD_ID, Telephony.Sms.READ, Telephony.Sms.SEEN);
        String[] args = new String[]{"1", "0", "0"};
        when(mockResolver.update(Telephony.Sms.CONTENT_URI, readContentValues, selection, args)).thenReturn(1);
        ConversationList mockConversations = mock(ConversationList.class);
        MarkReadTask task = new MarkReadTask(mockResolver, mockConversations, threeThreads(), readContentValues);

        task.call();

        verify(mockConversations).reloadConversations();
    }

    @Test
    public void doesntReloadConversationsWhenNothingIsUpdated() throws Exception {
        ContentResolver mockResolver = mock(ContentResolver.class);
        when(mockResolver.update(any(Uri.class), any(ContentValues.class), any(String.class), any(String[].class)))
            .thenReturn(0);
        ConversationList mockConversations = mock(ConversationList.class);
        MarkReadTask task = new MarkReadTask(mockResolver, mockConversations, onlyOneThread(), mock(ContentValues.class));

        task.call();

        verify(mockConversations, never()).reloadConversations();
    }

    private List<String> onlyOneThread() {
        return Collections.singletonList("1");
    }

    private List<String> threeThreads() {
        return Arrays.asList("0", "1", "2");
    }

}
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

package com.amlcurran.messages.conversationlist.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.PhoneNumberOnlyContact;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.data.ParcelablePhoneNumber;
import com.amlcurran.messages.loaders.fudges.ConversationListHelper;
import com.github.amlcurran.sourcebinder.CursorHelper;

import java.util.ArrayList;
import java.util.List;

class ConversationListLoader {
    private final ContentResolver contentResolver;
    private final Sort sort;
    private final ConversationListHelper helper;

    public ConversationListLoader(ContentResolver contentResolver, Sort sort, ConversationListHelper helper) {
        this.contentResolver = contentResolver;
        this.sort = sort;
        this.helper = helper;
    }

    List<Conversation> loadList(String query, String[] args) {
        final List<Conversation> conversations = new ArrayList<Conversation>();

        Cursor conversationsList = helper.queryConversationList(contentResolver, query, args, getSortString());

        while (conversationsList.moveToNext()) {

            String address = helper.getAddressFromRow(contentResolver, conversationsList);
            Contact contact = getContact(contentResolver, address);

            String body = CursorHelper.asString(conversationsList, helper.getSnippetCursorKey());
            boolean isRead = "1".equals(CursorHelper.asString(conversationsList, Telephony.Sms.Inbox.READ));
            String threadId = CursorHelper.asString(conversationsList, helper.getThreadIdCursorKey());
            boolean lastFromMe = CursorHelper.asInt(conversationsList, Telephony.Sms.TYPE) != Telephony.Sms.MESSAGE_TYPE_INBOX;
            long lastMessageTime = CursorHelper.asLong(conversationsList, Telephony.Sms.DATE_SENT);
            int conversationCount = -1;
            if (threadId != null) {
                conversationCount = helper.getConversationCount(contentResolver, threadId);
            }
            Conversation conversation = new Conversation(contact.getNumber(), body, threadId, isRead, contact, lastFromMe, Time.fromMillis(lastMessageTime), conversationCount);

            if (conversation.getThreadId() != null) {
                conversations.add(conversation);
            }
        }

        conversationsList.close();

        return conversations;
    }
    
    private Contact getContact(ContentResolver contentResolver, String address) {
        // Deal with cases where the number isn't actually a number
        if (!PhoneNumberUtils.isGlobalPhoneNumber(address)) {
            return new PhoneNumberOnlyContact(new ParcelablePhoneNumber(address));
        }


        Uri phoneLookupUri = createPhoneLookupUri(address);
        Cursor peopleCursor = contentResolver.query(phoneLookupUri, ContactFactory.VALID_PROJECTION, null, null, null);

        Contact contact;
        if (peopleCursor.moveToFirst()) {
            contact = ContactFactory.fromCursor(peopleCursor);
        } else {
            contact = ContactFactory.fromAddress(address);
        }
        peopleCursor.close();
        return contact;
    }

    private String getSortString() {
        String sortOrder;
        if (sort == Sort.UNREAD) {
            sortOrder = Telephony.Sms.READ + " ASC, " + Telephony.Sms.DEFAULT_SORT_ORDER;
        } else {
            sortOrder = Telephony.Sms.DEFAULT_SORT_ORDER;
        }
        return sortOrder;
    }

    private static Uri createPhoneLookupUri(String phoneRaw) {
        return Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(phoneRaw));
    }
}
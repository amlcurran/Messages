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

package com.amlcurran.messages.events;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.PhoneNumber;

public class BroadcastEventBus implements EventBus {

    private static final String BASE_BROADCAST = "com.amlcurran.messages";
    public static final String BROADCAST_MESSAGE_SENT = BASE_BROADCAST + ".broadcast_message_sent";
    public static final String BROADCAST_LIST_INVALIDATED = BASE_BROADCAST + ".BROADCAST_LIST_INVALIDATED";
    public static final String BROADCAST_MESSAGE_RECEIVED = BASE_BROADCAST + ".broadcast_message_received";
    public static final String BROADCAST_MESSAGE_SENDING = BASE_BROADCAST + ".broadcast_message_sending";
    public static final String BROADCAST_LIST_LOADED = BASE_BROADCAST + ".LIST_LOADED";
    public static final String BROADCAST_MESSAGE_DRAFT = BASE_BROADCAST + ".broadcast_message_drafted";
    static final String EXTRA_FILTER = "filter";
    private final LocalBroadcastManager broadcaster;

    public BroadcastEventBus(Context context) {
        broadcaster = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void postListInvalidated() {
        broadcaster.sendBroadcast(new Intent(BROADCAST_LIST_INVALIDATED));
    }

    @Override
    public void postMessageSent(PhoneNumber phoneNumber) {
        broadcaster.sendBroadcast(new Intent(BROADCAST_MESSAGE_SENT).putExtra(EXTRA_FILTER, phoneNumber.toString()));
    }

    @Override
    public void postMessageReceived(PhoneNumber phoneNumber) {
        broadcaster.sendBroadcast(new Intent(BROADCAST_MESSAGE_RECEIVED).putExtra(EXTRA_FILTER, phoneNumber.toString()));
    }

    @Override
    public void postMessageSending(InFlightSmsMessage message) {
        Intent broadcast = new Intent(BROADCAST_MESSAGE_SENDING);
        broadcast.putExtra("message", message);
        broadcast.putExtra(EXTRA_FILTER, message.getPhoneNumber().toString());
        broadcaster.sendBroadcast(broadcast);
    }

    @Override
    public void postListLoaded() {
        broadcaster.sendBroadcast(new Intent(BROADCAST_LIST_LOADED));
    }

    @Override
    public void postMessageDrafted(PhoneNumber phoneNumber) {
        broadcaster.sendBroadcast(new Intent(BROADCAST_MESSAGE_DRAFT).putExtra(EXTRA_FILTER, phoneNumber.toString()));
    }
}

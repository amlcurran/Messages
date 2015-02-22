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

import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.events.EventBus;

public class BroadcastEventBus implements EventBus {

    static final String EXTRA_FILTER = "filter";
    private final LocalBroadcastManager broadcaster;

    public BroadcastEventBus(Context context) {
        broadcaster = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void postMessageSent(PhoneNumber phoneNumber) {
        broadcaster.sendBroadcast(new Intent(BROADCAST_MESSAGE_SENT).putExtra(EXTRA_FILTER, phoneNumber.flatten()));
    }

    @Override
    public void postMessageReceived(PhoneNumber phoneNumber) {
        broadcaster.sendBroadcast(new Intent(BROADCAST_MESSAGE_RECEIVED).putExtra(EXTRA_FILTER, phoneNumber.flatten()));
    }

    @Override
    public void postListLoaded() {
        broadcaster.sendBroadcast(new Intent(BROADCAST_LIST_LOADED));
    }

    @Override
    public void postMessageDrafted(PhoneNumber phoneNumber) {
        broadcaster.sendBroadcast(new Intent(BROADCAST_MESSAGE_DRAFT).putExtra(EXTRA_FILTER, phoneNumber.flatten()));
    }
}

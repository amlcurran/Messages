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

public class BroadcastManagerEventBus implements EventBus {

    public static final String BROADCAST_MESSAGE_SENT = "broadcast_message_sent";
    public static final String ACTION_LIST_CHANGED = "com.amlcurran.messages.ACTION_LIST_CHANGED";
    public static final String BROADCAST_MESSAGE_RECEIVED = "broadcast_message_received";
    private final LocalBroadcastManager broadcaster;

    public BroadcastManagerEventBus(Context context) {
        broadcaster = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void postListChanged() {
        broadcaster.sendBroadcast(new Intent(ACTION_LIST_CHANGED));
    }

    @Override
    public void postMessageSent() {
        broadcaster.sendBroadcast(new Intent(BROADCAST_MESSAGE_SENT));
    }

    @Override
    public void postMessageReceived() {
        broadcaster.sendBroadcast(new Intent(BROADCAST_MESSAGE_RECEIVED));
    }
}

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.amlcurran.messages.core.events.Broadcast;
import com.amlcurran.messages.core.events.EventSubscriber;

public class BroadcastEventSubscriber extends BroadcastReceiver implements EventSubscriber {

    private Context context;
    private Listener listener;

    public BroadcastEventSubscriber(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void startListening(Broadcast... broadcasts) {
        LocalBroadcastManager.getInstance(context).registerReceiver(this, buildMessageFilter(broadcasts));
    }

    @Override
    public void stopListening() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onMessageReceived();
    }

    private IntentFilter buildMessageFilter(Broadcast[] broadcasts) {
        IntentFilter filter = new IntentFilter();
        for (Broadcast broadcast : broadcasts) {
            filter.addAction(broadcast.getAction());
        }
        return filter;
    }

    public interface Listener {
        void onMessageReceived();
    }

}

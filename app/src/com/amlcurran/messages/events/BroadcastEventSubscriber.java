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

import java.util.ArrayList;
import java.util.Collections;

public class BroadcastEventSubscriber extends BroadcastReceiver implements EventSubscriber {

    private final ArrayList<Broadcast> listeningBroadcasts;
    private final Context context;
    private final Listener listener;

    public BroadcastEventSubscriber(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
        this.listeningBroadcasts = new ArrayList<Broadcast>();
    }

    @Override
    public void startListening(Broadcast... broadcasts) {
        Collections.addAll(listeningBroadcasts, broadcasts);
        LocalBroadcastManager.getInstance(context).registerReceiver(this, buildMessageFilter(broadcasts));
    }

    @Override
    public void stopListening() {
        listeningBroadcasts.clear();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Broadcast matchedActionBroadcast = matchBroadcastWithAction(listeningBroadcasts, intent.getAction());
        if (matchedActionBroadcast != null && matchedActionBroadcast.hasFilter()) {
            // Only if the filter matches do we notify
            if (matchesFilter(intent, matchedActionBroadcast)) {
                listener.onMessageReceived();
            }
        } else {
            listener.onMessageReceived();
        }
    }

    private static boolean matchesFilter(Intent intent, Broadcast matchedActionBroadcast) {
        return matchedActionBroadcast.getFilter().equals(intent.getStringExtra(BroadcastEventBus.EXTRA_FILTER));
    }

    private static Broadcast matchBroadcastWithAction(ArrayList<Broadcast> broadcasts, String action) {
        for (Broadcast broadcast : broadcasts) {
            if (broadcast.getAction().equals(action)) {
                return broadcast;
            }
        }
        return null;
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

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

package com.amlcurran.messages;

import android.app.ListFragment;
import android.os.Bundle;

import com.espian.utils.AdaptiveCursorSource;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.espian.utils.ProviderHelper;
import com.espian.utils.SourceBinderAdapter;

/**
 * Defines a fragment that uses cursors from the Telephony API and listens to receiving of new messages
 */
public abstract class ListeningCursorListFragment<T> extends ListFragment implements LocalMessageReceiver.Listener {
    protected SourceBinderAdapter<T> adapter;
    protected AdaptiveCursorSource<T> source;
    private MessagesLoader messageLoader;
    private LocalMessageReceiver messageReceiver;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messageLoader = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class).get(getActivity()).getMessagesLoader();
        messageReceiver = new LocalMessageReceiver(getActivity(), this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData(messageLoader, false);
        messageReceiver.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        messageReceiver.stopListening();
    }

    public MessagesLoader getMessageLoader() {
        return messageLoader;
    }

    @Override
    public void onMessageReceived() {
        loadData(messageLoader, true);
    }

    public abstract void loadData(MessagesLoader loader, boolean isRefresh);

    protected final void onUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

}

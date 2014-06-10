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

package com.amlcurran.messages.threads;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amlcurran.messages.ListeningCursorListFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.loaders.ConversationListChangeListener;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.ArrayListSource;
import com.github.amlcurran.sourcebinder.SourceBinderAdapter;

import java.util.Calendar;
import java.util.List;

public class ThreadFragment extends ListeningCursorListFragment<SmsMessage> implements ThreadListener, ComposeMessageView.OnMessageComposedListener, ConversationListChangeListener {

    private static final String THREAD_ID = "threadId";
    private static final String ADDRESS = "address";

    private SmsComposeListener listener;
    private String sendAddress;
    private DefaultAppChecker defaultChecker;
    private ComposeMessageView composeView;
    private ArrayListSource<SmsMessage> source;

    public static ThreadFragment create(String threadId, String address) {
        Bundle bundle = new Bundle();
        bundle.putString(THREAD_ID, threadId);
        bundle.putString(ADDRESS, address);

        ThreadFragment fragment = new ThreadFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thread, container, false);
        composeView = ((ComposeMessageView) view.findViewById(R.id.thread_compose_view));
        composeView.setComposeListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = new ProviderHelper<SmsComposeListener>(SmsComposeListener.class).get(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        sendAddress = getArguments().getString(ADDRESS);
        source = new ArrayListSource<SmsMessage>();
        adapter = new SourceBinderAdapter<SmsMessage>(getActivity(), source, new ThreadBinder(getListView()));
        defaultChecker = new DefaultAppChecker(getActivity(), composeView);
        setListAdapter(adapter);
        getListView().setStackFromBottom(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        defaultChecker.checkSmsApp();
    }

    @Override
    public void loadData(MessagesLoader loader, boolean isRefresh) {
        loader.loadThread(getThreadId(), this);
    }

    @Override
    public String[] getActions() {
        return new String[] { BroadcastEventBus.BROADCAST_MESSAGE_SENDING };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_thread, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_call:
                listener.callNumber(sendAddress);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private String getThreadId() {
        return getArguments().getString(THREAD_ID);
    }

    private void scrollToBottom() {
        getListView().post(new Runnable() {
            @Override
            public void run() {
                getListView().smoothScrollToPosition(source.getCount() -1);
            }
        });
    }

    @Override
    public void onMessageComposed(CharSequence body) {
        String message = String.valueOf(body);
        long timestamp = Calendar.getInstance().getTimeInMillis();
        InFlightSmsMessage smsMessage = new InFlightSmsMessage(sendAddress, message, timestamp);
        listener.sendSms(smsMessage);
    }

    @Override
    public void listChanged() {
        new BroadcastEventBus(getActivity()).postListInvalidated();
    }

    @Override
    public void onThreadLoaded(final List<SmsMessage> messageList) {
        onUiThread(new Runnable() {
            @Override
            public void run() {
                source.replace(messageList);
                scrollToBottom();
                getMessageLoader().markThreadAsRead(getThreadId(), ThreadFragment.this);
            }
        });
    }

}

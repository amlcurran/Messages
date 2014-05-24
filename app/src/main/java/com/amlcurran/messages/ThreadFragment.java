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

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amlcurran.messages.data.SmsMessage;
import com.espian.utils.AdaptiveCursorSource;
import com.amlcurran.messages.loaders.CursorLoadListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.espian.utils.ProviderHelper;
import com.espian.utils.SimpleBinder;
import com.espian.utils.SourceBinderAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadFragment extends ListeningCursorListFragment<SmsMessage> implements CursorLoadListener, ComposeMessageView.OnMessageComposedListener {

    private static final String THREAD_ID = "threadId";
    private static final String ADDRESS = "address";

    private Listener listener;
    private String sendAddress;
    private DefaultAppChecker defaultChecker;
    private ComposeMessageView composeView;

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
        listener = new ProviderHelper<Listener>(Listener.class).get(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sendAddress = getArguments().getString(ADDRESS);
        source = new ThreadMessageAdaptiveCursorSource();
        adapter = new SourceBinderAdapter<SmsMessage>(getActivity(), source, new ThreadBinder());
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

    private String getThreadId() {
        return getArguments().getString(THREAD_ID);
    }

    @Override
    public void onCursorLoaded(final Cursor cursor) {
        onUiThread(new Runnable() {
            @Override
            public void run() {
                source.setCursor(cursor);
                adapter.notifyDataSetChanged();
                getMessageLoader().markThreadAsRead(getThreadId());
            }
        });
    }

    @Override
    public void onMessageComposed(CharSequence body) {
        listener.onSendMessage(sendAddress, String.valueOf(body));
    }

    private class ThreadBinder extends SimpleBinder<SmsMessage> {

        private static final int ITEM_ME = 0;
        private static final int ITEM_THEM = 1;
        private final DateFormat formatter = new SimpleDateFormat("HH:mm dd-MMM-yy");
        private final Date date = new Date();

        @Override
        public View bindView(View convertView, SmsMessage item, int position) {

            date.setTime(item.getTimestamp());

            TextView bodyText = getTextView(convertView, android.R.id.text1);
            bodyText.setText(item.getBody());
            getTextView(convertView, android.R.id.text2).setText(formatter.format(date));
            Linkify.addLinks(bodyText, Linkify.ALL);

            return convertView;
        }

        @Override
        public View createView(Context context, int itemViewType) {
            if (itemViewType == ITEM_ME) {
                return LayoutInflater.from(context).inflate(R.layout.item_thread_item_me, getListView(), false);
            } else if (itemViewType == ITEM_THEM) {
                return LayoutInflater.from(context).inflate(R.layout.item_thread_item_them, getListView(), false);
            }
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position, SmsMessage item) {
            return item.isFromMe() ? ITEM_ME : ITEM_THEM;
        }

        private TextView getTextView(View convertView, int text1) {
            return (TextView) convertView.findViewById(text1);
        }

    }

    private static class ThreadMessageAdaptiveCursorSource extends AdaptiveCursorSource<SmsMessage> {

        @Override
        public SmsMessage getFromCursorRow(Cursor cursor) {
            return SmsMessage.fromCursor(cursor);
        }
    }

    public interface Listener {
        void onSendMessage(String address, String message);
    }

}

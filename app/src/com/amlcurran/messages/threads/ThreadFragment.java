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
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.amlcurran.messages.MessagesActivity;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.ParcelablePhoneNumber;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.OnContactQueryListener;
import com.amlcurran.messages.preferences.PreferenceStoreDraftRepository;
import com.amlcurran.messages.telephony.CentralWriter;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.amlcurran.messages.ui.ContactView;
import com.amlcurran.messages.ui.CustomHeaderFragment;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.Source;
import com.github.amlcurran.sourcebinder.SourceBinderAdapter;

import java.util.Calendar;

public class ThreadFragment extends ListFragment implements
        ComposeMessageView.OnMessageComposedListener,
        CustomHeaderFragment, ThreadController.Callback {

    private static final String THREAD_ID = "threadId";
    private static final String ADDRESS = "address";
    private static final String CONTACT = "contact";

    private SmsComposeListener listener;
    private ParcelablePhoneNumber phoneNumber;
    private ComposeMessageView composeView;
    private ContactView contactView;
    private ThreadController threadController;

    public static ThreadFragment create(String threadId, String address, Bundle contactBundle) {
        Bundle bundle = new Bundle();
        bundle.putString(THREAD_ID, threadId);
        bundle.putString(ADDRESS, address);
        bundle.putBundle(CONTACT, contactBundle);

        ThreadFragment fragment = new ThreadFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thread, container, false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnScrollListener(listListener);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setStackFromBottom(true);
        listView.setDivider(null);
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
        phoneNumber = new ParcelablePhoneNumber(getArguments().getString(ADDRESS));
        threadController = new ThreadController(getThreadId(), phoneNumber, this);
        threadController.create(getActivity(), composeView);
        ((MessagesActivity) getActivity()).customHeader(this);

        setHasOptionsMenu(true);

        SourceBinderAdapter<SmsMessage> adapter = new SourceBinderAdapter<SmsMessage>(getActivity(), threadController.getSource(), new ThreadBinder(getListView()));
        setListAdapter(adapter);

        composeView.setText(retrieveDraft(phoneNumber));
        setUpContactView(phoneNumber);
    }

    private void setUpContactView(PhoneNumber phoneNumber) {
        Contact receivedContact = ContactFactory.desmooshContact(getArguments().getBundle(CONTACT));
        final MessagesLoader messagesLoader = SingletonManager.getMessagesLoader(getActivity());
        if (receivedContact == null) {
            messagesLoader.queryContact(phoneNumber.flatten(), new OnContactQueryListener() {
                @Override
                public void contactLoaded(final Contact contact) {
                    contactView.setContact(contact, messagesLoader);
                }
            });
        } else {
            contactView.setContact(receivedContact, messagesLoader);
        }
    }

    private String retrieveDraft(PhoneNumber phoneNumber) {
        return new PreferenceStoreDraftRepository(getActivity()).getDraft(phoneNumber);
    }

    private void saveDraft() {
        String text = composeView.getText();
        InFlightSmsMessage message = new InFlightSmsMessage(phoneNumber, text, System.currentTimeMillis());
        new CentralWriter(getActivity()).storeDraft(message);
    }

    @Override
    public void onResume() {
        super.onResume();
        threadController.resume();
    }

    @Override
    public void onStart() {
        super.onStart();
        threadController.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        threadController.stop();
        saveDraft();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_thread, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_call:
                listener.callNumber(phoneNumber);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private String getThreadId() {
        return getArguments().getString(THREAD_ID);
    }

    private void scrollTo(final int position) {
        getListView().post(new Runnable() {
            @Override
            public void run() {
                getListView().smoothScrollToPosition(position);
            }
        });
    }

    @Override
    public void onMessageComposed(CharSequence body) {
        String message = String.valueOf(body);
        long timestamp = Calendar.getInstance().getTimeInMillis();
        InFlightSmsMessage smsMessage = new InFlightSmsMessage(phoneNumber, message, timestamp);
        listener.sendSms(smsMessage);
        new CentralWriter(getActivity()).clearDraft(phoneNumber);
    }

    @Override
    public View getHeaderView(Context context) {
        contactView = new ContactView(context, null);
        return contactView;
    }

    private AbsListView.OnScrollListener listListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (totalItemCount > 0) {
                setUpViewShadow(listView, totalItemCount);
            }
        }
    };

    private void setUpViewShadow(AbsListView listView, int totalItemCount) {
        boolean lastVisibleIsLast = listView.getLastVisiblePosition() == totalItemCount - 1;
        boolean lastViewIsAtBottom = listView.getChildAt(listView.getChildCount() - 1).getBottom() == listView.getBottom();

        if (lastVisibleIsLast && lastViewIsAtBottom) {
            listView.setBackgroundResource(0);
        } else {
            listView.setBackgroundResource(R.drawable.compose_shadow_background);
        }
    }

    @Override
    public void dataLoaded(Source<SmsMessage> source) {
        scrollTo(source.getCount() - 1);
    }

}

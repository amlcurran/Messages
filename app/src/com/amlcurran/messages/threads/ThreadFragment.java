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

import android.animation.LayoutTransition;
import android.app.ListFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.bucket.BundleBuilder;
import com.amlcurran.messages.core.TextUtils;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.data.ParcelablePhoneNumber;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.OnContactQueryListener;
import com.amlcurran.messages.preferences.PreferenceStoreDraftRepository;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.amlcurran.messages.ui.CustomHeaderFragment;
import com.amlcurran.messages.ui.contact.ContactClickListener;
import com.amlcurran.messages.ui.contact.DefaultContactView;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.Source;
import com.github.amlcurran.sourcebinder.SourceBinderAdapter;

public class ThreadFragment extends ListFragment implements
        CustomHeaderFragment<DefaultContactView>, ThreadController.Callback {

    private static final String THREAD_ID = "threadId";
    private static final String ADDRESS = "address";
    private static final String CONTACT = "contact";
    private static final String COMPOSED_MESSAGE = "composed_message";

    private StandardComposeCallbacks composeCallbacks;
    private SmsComposeListener listener;
    private ParcelablePhoneNumber phoneNumber;
    private ComposeMessageView composeView;
    private DefaultContactView contactView;
    private ThreadController threadController;
    private DraftRepository draftRepository;
    private ListView listView;

    public static ThreadFragment create(String threadId, PhoneNumber address, Bundle contactBundle, String composedMessage) {
        Bundle bundle = new BundleBuilder()
                .put(THREAD_ID, threadId)
                .put(ADDRESS, address.flatten())
                .put(CONTACT, contactBundle)
                .put(COMPOSED_MESSAGE, composedMessage)
                .build();

        ThreadFragment fragment = new ThreadFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thread, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setOnScrollListener(new LegacyShadowingScrollListener());
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setStackFromBottom(true);
        listView.setDivider(null);
        listView.setAlpha(0);
        //listView.setLayoutTransition(createLayoutTransition(inflater.getContext().getResources()));
        composeView = ((ComposeMessageView) view.findViewById(R.id.thread_compose_view));
        return view;
    }

    private LayoutTransition createLayoutTransition(Resources resources) {
        LayoutTransition transition = new LayoutTransition();
        int shortDuration = resources.getInteger(android.R.integer.config_shortAnimTime);
        transition.setDuration(shortDuration);
        return transition;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = new ProviderHelper<SmsComposeListener>(SmsComposeListener.class).get(getActivity());
        phoneNumber = new ParcelablePhoneNumber(getArguments().getString(ADDRESS));
        String threadId = getArguments().getString(THREAD_ID);

        draftRepository = new PreferenceStoreDraftRepository(getActivity());
        composeCallbacks = new StandardComposeCallbacks(getActivity(), phoneNumber, listener);
        composeView.setComposeListener(composeCallbacks);
        threadController = new ThreadController(threadId, phoneNumber, this);
        threadController.create(getActivity(), composeView);

        prefillComposeView();

        setHasOptionsMenu(true);

        ThreadBinder.ResendCallback resendCallback = new DeleteFailedResender(getActivity(), composeCallbacks);
        ThreadBinder threadBinder = new ThreadBinder(getListView(), getResources(), resendCallback);
        SourceBinderAdapter<SmsMessage> adapter = new SourceBinderAdapter<SmsMessage>(getActivity(), threadController.getSource(), threadBinder);
        setListAdapter(adapter);

        setUpContactView(phoneNumber);
    }

    private void prefillComposeView() {
        String composedMessage = getArguments().getString(COMPOSED_MESSAGE);
        if (TextUtils.isNotEmpty(composedMessage)) {
            composeView.setText(composedMessage);
        } else {
            composeView.setText(retrieveDraft(phoneNumber));
        }
    }

    private void setUpContactView(PhoneNumber phoneNumber) {
        Contact receivedContact = ContactFactory.desmooshContact(getArguments().getBundle(CONTACT));
        final MessagesLoader messagesLoader = SingletonManager.getMessagesLoader(getActivity());
        if (receivedContact == null) {
            messagesLoader.queryContact(phoneNumber, new OnContactQueryListener() {
                @Override
                public void contactLoaded(final Contact contact) {
                    bindContactToView(contact, messagesLoader);
                }
            });
        } else {
            bindContactToView(receivedContact, messagesLoader);
        }
    }

    private void bindContactToView(Contact receivedContact, MessagesLoader messagesLoader) {
        getHeaderView(getActivity()).setContact(receivedContact, messagesLoader);
        getHeaderView(getActivity()).setClickToView(((ContactClickListener) getActivity()), true);
    }

    private String retrieveDraft(PhoneNumber phoneNumber) {
        return draftRepository.getDraft(phoneNumber);
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
        threadController.saveDraft(draftRepository, composeView.getText());
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

    private void scrollTo(final int position) {
        getListView().post(new Runnable() {
            @Override
            public void run() {
                getListView().smoothScrollToPosition(position);
            }
        });
    }

    @Override
    public DefaultContactView getHeaderView(Context context) {
        if (contactView == null) {
            contactView = new DefaultContactView(context, null);
        }
        return contactView;
    }

    @Override
    public void dataLoaded(Source<SmsMessage> source) {
        listView.animate()
                .alpha(1)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
        scrollTo(source.getCount() - 1);
    }

}

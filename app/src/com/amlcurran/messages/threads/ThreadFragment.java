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

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.bucket.BundleBuilder;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.events.EventSubscriber;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.events.BroadcastEventSubscriber;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.amlcurran.messages.ui.CustomHeaderFragment;
import com.amlcurran.messages.ui.contact.ContactClickListener;
import com.amlcurran.messages.ui.contact.DefaultContactView;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.SourceBinderAdapter;

public class ThreadFragment extends ListFragment implements
        CustomHeaderFragment<DefaultContactView>, ThreadController.ThreadView, ThreadView {

    private static final String THREAD_ID = "threadId";
    private static final String CONTACT = "contact";
    private static final String COMPOSED_MESSAGE = "composed_message";

    private ComposeMessageView composeView;
    private DefaultContactView contactView;
    private ThreadController threadController;
    private ListView listView;

    public static ThreadFragment create(String threadId, @NonNull Bundle contactBundle, String composedMessage) {
        Bundle bundle = new BundleBuilder()
                .put(THREAD_ID, threadId)
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
        composeView = ((ComposeMessageView) view.findViewById(R.id.thread_compose_view));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Contact contact = ContactFactory.desmooshContact(getArguments().getBundle(CONTACT));
        String threadId = getArguments().getString(THREAD_ID);

        DependencyRepository dependencyRepository = ((DependencyRepository) getActivity());
        EventSubscriber messageReceiver = new BroadcastEventSubscriber(getActivity());
        DefaultAppChecker defaultChecker = new DefaultAppChecker(getActivity());

        threadController = new ThreadController(threadId, contact, getArguments().getString(COMPOSED_MESSAGE), this, messageReceiver, defaultChecker, dependencyRepository);

        SmsComposeListener listener = new ProviderHelper<SmsComposeListener>(SmsComposeListener.class).get(getActivity());
        StandardComposeCallbacks composeCallbacks = new StandardComposeCallbacks(getActivity(), contact.getNumber(), listener);
        composeView.setComposeListener(composeCallbacks);

        setHasOptionsMenu(true);

        ThreadBinder.ResendCallback resendCallback = new DeleteFailedResender(getActivity(), composeCallbacks);
        ThreadBinder threadBinder = new ThreadBinder(getListView(), getResources(), resendCallback);
        SourceBinderAdapter<SmsMessage> adapter = new SourceBinderAdapter<SmsMessage>(getActivity(), threadController.getSource(), threadBinder);
        setListAdapter(adapter);
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_thread, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return threadController.menuItemClicked(item) || super.onOptionsItemSelected(item);
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
    public void showThreadList(int count) {
        if (isAdded()) {
            listView.animate()
                    .alpha(1)
                    .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                    .start();
            scrollTo(count - 1);
        }
    }

    @Override
    public void bindContactToHeader(Contact contact) {
        getHeaderView(getActivity()).setContact(contact, SingletonManager.getMessagesLoader(getActivity()));
        getHeaderView(getActivity()).setClickToView(((ContactClickListener) getActivity()), true);
    }

    @Override
    public String getComposedMessage() {
        return composeView.getText();
    }

    @Override
    public void setComposedMessage(String composedMessage) {
        composeView.setText(composedMessage);
    }

    @Override
    public void isDefaultSmsApp() {
        composeView.isDefaultSmsApp();
    }

    @Override
    public void isNotDefaultSmsApp() {
        composeView.isNotDefaultSmsApp();
    }
}

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

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.bucket.BundleBuilder;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.events.EventSubscriber;
import com.amlcurran.messages.core.threads.Thread;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.events.BroadcastEventSubscriber;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.threads.binder.ThreadRecyclerBinder;
import com.amlcurran.messages.threads.binder.ViewHolder;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.amlcurran.messages.ui.CustomHeaderFragment;
import com.amlcurran.messages.ui.contact.DefaultRoundContactView;
import com.amlcurran.sourcebinder.recyclerview.RecyclerSourceBinderAdapter;

public class ThreadFragment extends Fragment implements
        CustomHeaderFragment<DefaultRoundContactView>, ThreadViewController.ThreadView {

    static final String THREAD_ID = "threadId";
    static final String CONTACT = "contact";
    static final String COMPOSED_MESSAGE = "composed_message";

    private ComposeMessageView composeView;
    private DefaultRoundContactView contactView;
    private ThreadViewController threadViewController;
    private RecyclerView recyclerView;
    private ComposeViewController composeViewController;

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
        View view = inflater.inflate(R.layout.fragment_thread_recycler, container, false);
        recyclerView = ((RecyclerView) view.findViewById(R.id.thread_recycler));
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
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

        com.amlcurran.messages.core.threads.Thread thread = new Thread(dependencyRepository.getMessagesLoader(), messageReceiver, contact.getNumber(), threadId, SingletonManager.getMessageTransport(getActivity()));

        composeViewController = new ComposeViewController(composeView, dependencyRepository.getDraftRepository(), contact.getNumber(), getArguments().getString(COMPOSED_MESSAGE),
                defaultChecker);
        threadViewController = new ThreadViewController(thread, contact, this, dependencyRepository, new HandlerScheduledQueue(new Handler(Looper.getMainLooper())));
        composeView.setComposeListener(threadViewController);
        if (threadId == null) {
            composeViewController.cannotSendToSender();
        }

        setHasOptionsMenu(true);

        ResendCallback resendCallback = new Resender(getActivity());
        ThreadRecyclerBinder binder = new ThreadRecyclerBinder(getResources(), resendCallback);
        RecyclerSourceBinderAdapter<SmsMessage, ViewHolder> binderAdapter =
                new RecyclerSourceBinderAdapter<>(threadViewController.getSource(), binder);
        recyclerView.setAdapter(binderAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        threadViewController.start();
        composeViewController.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        threadViewController.stop();
        composeViewController.stop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_thread, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return threadViewController.menuItemClicked(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    public DefaultRoundContactView getHeaderView(Context context) {
        if (contactView == null) {
            contactView = new DefaultRoundContactView(context, null);
        }
        return contactView;
    }

    @Override
    public void bindContactToHeader(Contact contact) {
        if (getActivity() != null) {
            getHeaderView(getActivity()).setContact(contact, SingletonManager.getPhotoLoader(getActivity()));
            getHeaderView(getActivity()).setClickToView(new DefaultContactClickListener((DependencyRepository) getActivity()));
        }
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public void scrollTo(final int position) {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(position);
            }
        });
    }

    private class HandlerScheduledQueue implements ScheduledQueue {

        private final Handler handler;

        private HandlerScheduledQueue(Handler handler) {
            this.handler = handler;
        }

        @Override
        public Runnable executeWithDelay(Runnable runnable, long millisDelay) {
            handler.postDelayed(runnable, millisDelay);
            return runnable;
        }

        @Override
        public void removeEvents(Runnable runnable) {
            handler.removeCallbacks(runnable);
        }
    }
}

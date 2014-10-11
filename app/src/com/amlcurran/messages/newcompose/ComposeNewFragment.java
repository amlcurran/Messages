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

package com.amlcurran.messages.newcompose;

import android.animation.LayoutTransition;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.amlcurran.messages.ui.contact.ContactChipView;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.ArrayListSource;
import com.github.amlcurran.sourcebinder.SourceBinderAdapter;

import java.util.List;

public class ComposeNewFragment extends Fragment implements ContactChipView.RemoveListener,
    ComposeNewView {

    static final String EXTRA_ADDRESS = "address";
    static final String EXTRA_MESSAGE = "message";
    private ComposeMessageView composeView;
    private EditText pickPersonView;
    private DefaultAppChecker defaultAppChecker;
    private SmsComposeListener listener;
    private ArrayListSource<Contact> contactSource;
    private AbsListView personListView;
    private SourceBinderAdapter<Contact> adapter;
    private MessagesLoader loader;
    private RecipientChooser recipientChooser;
    private ContactChipView contactChip;
    private ComposeNewController composeNewController;

    public static ComposeNewFragment withAddress(String sendAddress) {
        ComposeNewFragment newFragment = new ComposeNewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ADDRESS, sendAddress);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    public static ComposeNewFragment withMessage(String message) {
        ComposeNewFragment newFragment = new ComposeNewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MESSAGE, message);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    public ComposeNewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_new, container, false);
        contactChip = (ContactChipView) view.findViewById(R.id.new_compose_chip);
        contactChip.setRemoveListener(this);
        composeView = (ComposeMessageView) view.findViewById(R.id.new_compose_view);
        pickPersonView = ((EditText) view.findViewById(R.id.new_pick_person));
        personListView = ((AbsListView) view.findViewById(R.id.new_person_list));

        // Set-up the layout transitions
        LayoutTransition layoutTransition = ((ViewGroup) view.findViewById(R.id.recipient_view_host)).getLayoutTransition();
        layoutTransition.setDuration(150);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DependencyRepository dependencyRepository = (DependencyRepository) getActivity();
        listener = new ProviderHelper<SmsComposeListener>(SmsComposeListener.class).get(getActivity());
        composeNewController = new ComposeNewController(this, dependencyRepository, listener);
        defaultAppChecker = new DefaultAppChecker(getActivity());
        loader = new ProviderHelper<MessagesLoader.Provider>(MessagesLoader.Provider.class).get(getActivity()).getMessagesLoader();
        contactSource = new ArrayListSource<Contact>();

        adapter = new SourceBinderAdapter<Contact>(getActivity(), contactSource, new ContactBinder(loader));
        recipientChooser = new RecipientChooser(composeNewController);
        composeView.setComposeListener(composeNewController);

        personListView.setAdapter(adapter);
        personListView.setOnItemClickListener(recipientChooser);
        personListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        composeNewController.create(getArguments());
        loader.loadContacts(new DelayedDataLoader());
    }

    @Override
    public void onResume() {
        super.onResume();
        defaultAppChecker.checkSmsApp(composeView);
    }

    @Override
    public CharSequence getEnteredAddress() {
        return pickPersonView.getText();
    }

    @Override
    public void sendFailedWithInvalidRecipient() {
        Toast.makeText(getActivity(), "Enter a valid recipient", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setComposedMessage(String preparedMessage) {
        composeView.setText(preparedMessage);
    }

    @Override
    public void setRecipient(String preparedAddress) {
        pickPersonView.setText(preparedAddress);
    }

    @Override
    public void chipRemoveRequested(ContactChipView contactChipView, Contact contact) {
        contactChipView.setVisibility(View.GONE);
        pickPersonView.setText("");
    }

    @Override
    public void chosenContact(Contact contact) {
        contactChip.setVisibility(View.VISIBLE);
        contactChip.setContact(contact, SingletonManager.getMessagesLoader(getActivity()));
        pickPersonView.setText(contact.getNumber().flatten());
    }

    @Override
    public String getComposedMessage() {
        return composeView.getText();
    }

    private class ReplaceDataRunnable implements Runnable {
        private final List<Contact> contacts;

        public ReplaceDataRunnable(List<Contact> contacts) {
            this.contacts = contacts;
        }

        @Override
        public void run() {
            contactSource.replace(contacts);
            recipientChooser.setContacts(contacts);
        }
    }

    private class DelayedDataLoader implements ContactListListener {

        @Override
        public void contactListLoaded(final List<Contact> contacts) {
            new Handler().postDelayed(new ReplaceDataRunnable(contacts), getResources().getInteger(android.R.integer.config_shortAnimTime) + 100);
        }
    }
}

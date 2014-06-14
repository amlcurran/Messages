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
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.loaders.ContactListListener;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.ArrayListSource;
import com.github.amlcurran.sourcebinder.SimpleBinder;
import com.github.amlcurran.sourcebinder.SourceBinderAdapter;

import java.util.Calendar;
import java.util.List;

public class ComposeNewFragment extends Fragment implements ComposeMessageView.OnMessageComposedListener, TextWatcher, RecipientChooser.ChooserListener {

    private static final String EXTRA_ADDRESS = "address";
    private static final String EXTRA_MESSAGE = "message";
    private ComposeMessageView composeView;
    private EditText pickPersonView;
    private DefaultAppChecker defaultAppChecker;
    private SmsComposeListener listener;
    private ArrayListSource<Contact> contactSource;
    private ListView personListView;
    private SourceBinderAdapter<Contact> adapter;
    private MessagesLoader loader;
    private RecipientChooser recipientChooser;

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
        composeView = (ComposeMessageView) view.findViewById(R.id.new_compose_view);
        composeView.setComposeListener(this);
        pickPersonView = ((EditText) view.findViewById(R.id.new_pick_person));
        pickPersonView.addTextChangedListener(this);
        personListView = ((ListView) view.findViewById(R.id.new_person_list));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        defaultAppChecker = new DefaultAppChecker(getActivity(), composeView);
        contactSource = new ArrayListSource<Contact>();

        adapter = new SourceBinderAdapter<Contact>(getActivity(), contactSource, new ContactBinder());
        recipientChooser = new RecipientChooser(this);

        personListView.setAdapter(adapter);
        personListView.setOnItemClickListener(recipientChooser);
        personListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if (hasPreparedAddress()) {
            pickPersonView.setText(getPreparedAddress());
        }
        if (hasPreparedMessage()) {
            composeView.setText(getPreparedMessage());
        }

        loadContacts();
    }

    private String getPreparedMessage() {
        return getArguments().getString(EXTRA_MESSAGE);
    }

    private boolean hasPreparedMessage() {
        return getArguments() != null && getArguments().containsKey(EXTRA_MESSAGE);
    }

    private boolean hasPreparedAddress() {
        return getArguments() != null && getArguments().containsKey(EXTRA_ADDRESS);
    }

    private void loadContacts() {
        loader.loadContacts(new ContactListListener() {

            @Override
            public void contactListLoaded(final List<Contact> contacts) {
                onUiThread(new Runnable() {

                    @Override
                    public void run() {
                        contactSource.replace(contacts);
                        recipientChooser.setContacts(contacts);
                    }
                });
            }
        });
    }

    private void onUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = new ProviderHelper<SmsComposeListener>(SmsComposeListener.class).get(activity);
        loader = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class).get(activity).getMessagesLoader();
    }

    @Override
    public void onResume() {
        super.onResume();
        defaultAppChecker.checkSmsApp();
    }

    @Override
    public void onMessageComposed(CharSequence body) {
        if (isValid(getEnteredAddress())) {
            String address = String.valueOf(getEnteredAddress());
            String message = String.valueOf(body);
            long timestamp = Calendar.getInstance().getTimeInMillis();
            InFlightSmsMessage smsMessage = new InFlightSmsMessage(address, message, timestamp);
            listener.sendSms(smsMessage);
        } else {
            Toast.makeText(getActivity(), "Enter a valid recipient", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isValid(CharSequence address) {
        return PhoneNumberUtils.isWellFormedSmsAddress(String.valueOf(address));
    }

    private CharSequence getEnteredAddress() {
        return pickPersonView.getText();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        refreshSuggestions();
    }

    private void refreshSuggestions() {

    }

    @Override
    public void recipientChosen(Contact contact) {
        pickPersonView.setText(contact.getNumber());
    }

    public String getPreparedAddress() {
        return getArguments().getString(EXTRA_ADDRESS);
    }

    private class ContactBinder extends SimpleBinder<Contact> {

        @Override
        public View bindView(View convertView, Contact item, int position) {
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(item.getDisplayName());
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(item.getNumber());
            return convertView;
        }

        @Override
        public View createView(Context context, int itemViewType, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        }
    }

}

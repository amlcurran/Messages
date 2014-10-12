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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SmsComposeListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.SourceBinderAdapter;

public class ComposeNewFragment extends Fragment implements ComposeNewView {

    static final String EXTRA_ADDRESS = "address";
    static final String EXTRA_MESSAGE = "message";
    private ComposeMessageView composeView;
    private AbsListView personListView;
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
        composeView = (ComposeMessageView) view.findViewById(R.id.new_compose_view);
        composeView.setComposeListener(new NotifyControllerComposeListener());
        personListView = ((AbsListView) view.findViewById(R.id.new_person_list));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DependencyRepository dependencyRepository = (DependencyRepository) getActivity();
        SmsComposeListener listener = new ProviderHelper<SmsComposeListener>(SmsComposeListener.class).get(getActivity());
        DefaultAppChecker defaultAppChecker = new DefaultAppChecker(getActivity());
        PersonPicker personPicker = ((PersonPicker) getView().findViewById(R.id.new_person_picker));
        composeNewController = new ComposeNewController(this, personPicker, dependencyRepository, listener, defaultAppChecker, getResources());
        composeNewController.create(getArguments().getString(EXTRA_ADDRESS), getArguments().getString(EXTRA_MESSAGE));

        SourceBinderAdapter<Contact> adapter = new SourceBinderAdapter<Contact>(getActivity(), composeNewController.getSource(), new ContactBinder(dependencyRepository.getMessagesLoader()));
        personListView.setAdapter(adapter);
        personListView.setOnItemClickListener(new NotifyControllerClickListener());
        personListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        composeNewController.resume();
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
    public String getComposedMessage() {
        return composeView.getText();
    }

    @Override
    public void isDefaultSmsApp() {
        composeView.isDefaultSmsApp();
    }

    @Override
    public void isNotDefaultSmsApp() {
        composeView.isNotDefaultSmsApp();
    }

    private class NotifyControllerClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            composeNewController.personSelected(position);
        }
    }

    private class NotifyControllerComposeListener implements ComposeMessageView.ComposureCallbacks {
        @Override
        public void onMessageComposed(CharSequence body) {
            composeNewController.onMessageComposed(body);
        }
    }
}

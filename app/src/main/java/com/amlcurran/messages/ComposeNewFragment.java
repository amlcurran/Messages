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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.ui.ComposeMessageView;
import com.espian.utils.ProviderHelper;
import com.espian.utils.data.Binder;
import com.espian.utils.data.ListArraySource;
import com.espian.utils.data.SimpleBinder;
import com.espian.utils.data.Source;
import com.espian.utils.data.SourceBinderAdapter;

public class ComposeNewFragment extends Fragment implements ComposeMessageView.OnMessageComposedListener {

    private ComposeMessageView composeView;
    private AutoCompleteTextView pickPersonView;
    private DefaultAppChecker defaultAppChecker;
    private SmsComposeListener listener;
    private ListArraySource<Contact> contactSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_new, container, false);
        composeView = (ComposeMessageView) view.findViewById(R.id.new_compose_view);
        composeView.setComposeListener(this);
        pickPersonView = ((AutoCompleteTextView) view.findViewById(R.id.new_pick_person));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        defaultAppChecker = new DefaultAppChecker(getActivity(), composeView);
        contactSource = new ListArraySource<Contact>();

        ContactAdapter adapter = new ContactAdapter(getActivity(), contactSource, new ContactBinder(getActivity()));
        pickPersonView.setAdapter(adapter);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = new ProviderHelper<SmsComposeListener>(SmsComposeListener.class).get(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        defaultAppChecker.checkSmsApp();
    }

    @Override
    public void onMessageComposed(CharSequence body) {
        CharSequence address = getEnteredAddress();
        if (isValid(address)) {
            listener.sendSms(String.valueOf(address), String.valueOf(body));
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

    private class ContactAdapter extends SourceBinderAdapter<Contact> implements Filterable {

        public ContactAdapter(Context context, Source<Contact> source, Binder<Contact> binder) {
            super(context, source, binder);
        }

        @Override
        public Filter getFilter() {
            return new ContactFilter();
        }
    }

    private class ContactBinder extends SimpleBinder<Contact> {

        public ContactBinder(Activity activity) {
        }

        @Override
        public View bindView(View convertView, Contact item, int position) {
            return null;
        }

        @Override
        public View createView(Context context, int itemViewType) {
            return null;
        }
    }

    private class ContactFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    }
}

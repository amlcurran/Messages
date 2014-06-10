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
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;

import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.conversationlist.ConversationModalMarshall;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.loaders.ConversationListChangeListener;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.events.EventBus;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.loaders.OnContactQueryListener;
import com.amlcurran.messages.loaders.OnThreadDeleteListener;
import com.amlcurran.messages.reporting.EasyTrackerStatReporter;
import com.amlcurran.messages.reporting.NullStatReporter;
import com.amlcurran.messages.reporting.StatReporter;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.threads.ThreadFragment;
import com.amlcurran.messages.ui.FragmentController;
import com.amlcurran.messages.ui.MasterDetailFragmentController;
import com.amlcurran.messages.ui.SlidingPaneViewController;
import com.amlcurran.messages.ui.ViewController;
import com.google.analytics.tracking.android.EasyTracker;

import java.util.List;

public class MessagesActivity extends Activity implements MessagesLoaderProvider,
        ConversationListFragment.Listener, SmsComposeListener,
        DefaultAppChecker.Callback, SlidingPaneViewController.Callback, ConversationModalMarshall.Callback, OnThreadDeleteListener, ConversationListChangeListener, FragmentController.Callback, MenuController.Callbacks {

    private StatReporter statReporter;
    private FragmentController fragmentController;
    private ViewController viewController;
    private ActivityController activityController;
    private MenuController menuController;
    private DefaultAppChecker appChecker;
    private EventBus eventBus;
    private LaunchAssistant launchHelper = new LaunchAssistant();
    private boolean isSecondaryVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentController = new MasterDetailFragmentController(this, this);
        activityController = new ActivityController(this);
        viewController =     new SlidingPaneViewController(this, this);
        menuController =     new MenuController(this, this);
        viewController.setContentView();

        statReporter = new EasyTrackerStatReporter(EasyTracker.getInstance(this));
        appChecker = new DefaultAppChecker(this, this);
        eventBus = new BroadcastEventBus(this);

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .build());
            statReporter = new NullStatReporter();
        }

        Launch launch = launchHelper.getLaunchType(savedInstanceState, getIntent());

        switch (launch) {

            case FIRST_START:
                firstStart();
                break;

            case ANONYMOUS_SEND:
                anonymousSend();
                break;

            case SEND_TO:
                Uri data = getIntent().getData();
                String sendAddress = data.getSchemeSpecificPart();
                sendTo(sendAddress);
                break;

        }

    }

    private void sendTo(String sendAddress) {
        fragmentController.replaceFragment(ComposeNewFragment.withAddress(sendAddress), false);
    }

    private void anonymousSend() {
        fragmentController.replaceFragment(new ComposeNewFragment(), false);
    }

    private void firstStart() {
        fragmentController.loadEmptyFragment();
        fragmentController.loadMessagesListFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        statReporter.activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        statReporter.activityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessagesApp.getNotifier(this).clearNewMessagesNotification();
        appChecker.checkSmsApp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return menuController.create(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return menuController.prepare(menu, isSecondaryVisible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menuController.itemSelected(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    public void showSettings() {
        statReporter.sendUiEvent("settings");
        fragmentController.showSettings();
    }

    @Override
    public void showAbout() {
        activityController.showAbout();
    }

    @Override
    public void showConversationList() {
        statReporter.sendUiEvent("home_button");
        viewController.hideSecondary();
    }

    @Override
    public void composeNewMessage() {
        fragmentController.loadComposeNewFragment();
    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return MessagesApp.getMessagesLoader(this);
    }

    @Override
    public void onBackPressed() {
        if (!viewController.backPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConversationSelected(Conversation conversation) {
        ThreadFragment fragment = ThreadFragment.create(conversation.getThreadId(), conversation.getAddress());
        fragmentController.replaceFragment(fragment, false);
    }

    @Override
    public void sendSms(InFlightSmsMessage smsMessage) {
        activityController.sendSms(smsMessage);
    }

    @Override
    public void callNumber(String sendAddress) {
        statReporter.sendUiEvent("call_number");
        activityController.callNumber(sendAddress);
    }

    @Override
    public void isDefaultSmsApp() {
        viewController.hideDisabledBanner();
    }

    @Override
    public void isNotDefaultSmsApp() {
        viewController.showDisabledBanner();
    }

    @Override
    public void secondaryVisible() {
        isSecondaryVisible = true;
        menuController.update();
    }

    @Override
    public void secondaryHidden() {
        isSecondaryVisible = false;
        menuController.update();
    }

    @Override
    public void defaultsBannerPressed() {
        activityController.switchSmsApp();
    }

    @Override
    public void viewContact(String address) {
        statReporter.sendUiEvent("view_contact");
        getMessagesLoader().queryContact(address, new OnContactQueryListener() {

            @Override
            public void contactLoaded(Contact contact) {
                Uri lookupUri = ContactsContract.Contacts.getLookupUri(contact.getContactId(), contact.getLookupKey());
                Uri contactUri = ContactsContract.Contacts.lookupContact(getContentResolver(), lookupUri);
                activityController.viewContact(contactUri);
            }
        });
    }

    @Override
    public void deleteThreads(List<Conversation> conversationList) {
        getMessagesLoader().deleteThreads(conversationList, this);
    }

    @Override
    public void markAsUnread(List<Conversation> threadId) {
        statReporter.sendUiEvent("mark_thread_unread");
        getMessagesLoader().markThreadAsUnread(threadId, this);
    }

    @Override
     public void threadDeleted(final List<Conversation> deletedConversations) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewController.deletedConversations(deletedConversations);
                eventBus.postListInvalidated();
            }
        });
    }

    @Override
    public void listChanged() {
        eventBus.postListInvalidated();
    }

    @Override
    public void insertedDetail() {
        viewController.showSecondary();
    }

    @Override
    public void insertedMaster() {
        viewController.hideSecondary();
    }

}

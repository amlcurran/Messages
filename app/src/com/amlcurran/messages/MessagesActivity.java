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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.conversationlist.ConversationModalMarshall;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.loaders.ConversationListChangeListener;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.data.PhoneNumber;
import com.amlcurran.messages.events.EventBus;
import com.amlcurran.messages.launch.IntentDataExtractor;
import com.amlcurran.messages.launch.Launch;
import com.amlcurran.messages.launch.LaunchAssistant;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.loaders.OnContactQueryListener;
import com.amlcurran.messages.loaders.OnThreadDeleteListener;
import com.amlcurran.messages.notifications.BlockingInUiDialogNotifier;
import com.amlcurran.messages.notifications.BlockingInUiNotifier;
import com.amlcurran.messages.notifications.Dialog;
import com.amlcurran.messages.notifications.InUiNotifier;
import com.amlcurran.messages.notifications.InUiToastNotifier;
import com.amlcurran.messages.preferences.PreferenceStore;
import com.amlcurran.messages.reporting.StatReporter;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.threads.ThreadFragment;
import com.amlcurran.messages.ui.FragmentController;
import com.amlcurran.messages.ui.SinglePaneViewController;
import com.amlcurran.messages.ui.SlidingPaneViewController;
import com.amlcurran.messages.ui.ViewController;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MessagesActivity extends Activity implements MessagesLoaderProvider,
        ConversationListFragment.Listener, SmsComposeListener,
        DefaultAppChecker.Callback, SlidingPaneViewController.Callback, ConversationModalMarshall.Callback,
        OnThreadDeleteListener, ConversationListChangeListener, FragmentController.Callback, MenuController.Callbacks {

    private InUiNotifier toastInUiNotifier;
    private StatReporter statReporter;
    private FragmentController fragmentController;
    private ViewController viewController;
    private ActivityController activityController;
    private MenuController menuController;
    private DefaultAppChecker appChecker;
    private EventBus eventBus;
    private LaunchAssistant launchHelper = new LaunchAssistant();
    private boolean isSecondaryVisible;
    private MessagesLoader messagesLoader;
    private BlockingInUiNotifier blockingInUiNotifier;
    private PreferenceStore preferencesStore;
    private HoloActionBarController actionBarController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewController      = new SinglePaneViewController(this);
        fragmentController  = new SinglePaneFragmentController(getFragmentManager(), viewController, this, this);
        toastInUiNotifier   = new InUiToastNotifier(this);
        blockingInUiNotifier= new BlockingInUiDialogNotifier(getFragmentManager());
        activityController  = new ActivityController(this, blockingInUiNotifier);
        messagesLoader      = SingletonManager.getMessagesLoader(this);
        statReporter        = SingletonManager.getStatsReporter(this);;
        eventBus            = SingletonManager.getEventBus(this);
        preferencesStore    = new PreferenceStore(this);
        actionBarController = new HoloActionBarController(getActionBar());

        viewController.setContentView(this);

        menuController = new MenuController(this, this);
        appChecker = new DefaultAppChecker(this, this);

        handleLaunch(savedInstanceState, getIntent(), preferencesStore);
    }

    private void handleLaunch(Bundle savedInstanceState, Intent intent, PreferenceStore preferencesStore) {
        Launch launch = launchHelper.getLaunchType(savedInstanceState, intent, preferencesStore);
        IntentDataExtractor intentDataExtractor = new IntentDataExtractor(intent);

        switch (launch) {

            case FIRST_START:
                firstStart();
                break;

            case SEND_ANONYMOUS:
                anonymousSend();
                break;

            case SEND_TO:
                sendTo(intentDataExtractor.getAddressFromUri());
                break;

            case SHARE_TO:
                anonymousSendWithMessage(intentDataExtractor.getMessage());
                break;

            case VIEW_CONVERSATION:
                viewConversation(intentDataExtractor.getThreadId(), intentDataExtractor.getAddress());
                break;

            case MMS_TO:
                displayMmsError();
                break;

            case SHOW_ALPHA_MESSAGE:
                firstStart();
                showFirstDialog();
                break;

        }
    }

    private void showFirstDialog() {
        blockingInUiNotifier.show(new BlockingInUiNotifier.Callbacks() {
            @Override
            public void positive() {

            }

            @Override
            public void negative() {

            }
        }, getString(R.string.alpha_title), getString(R.string.alpha_message),
                new Dialog.Button("OK"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    private void displayMmsError() {
        fragmentController.loadConversationListFragment();
        fragmentController.replaceFragment(new MmsErrorFragment(), false);
    }

    private void viewConversation(String threadId, String address) {
        fragmentController.loadConversationListFragment();
        fragmentController.replaceFragment(ThreadFragment.create(threadId, address), false);
    }

    private void anonymousSendWithMessage(String message) {
        fragmentController.loadConversationListFragment();
        fragmentController.replaceFragment(ComposeNewFragment.withMessage(message), false);
    }

    private void sendTo(String sendAddress) {
        fragmentController.loadConversationListFragment();
        fragmentController.replaceFragment(ComposeNewFragment.withAddress(sendAddress), false);
    }

    private void anonymousSend() {
        fragmentController.loadConversationListFragment();
        fragmentController.replaceFragment(new ComposeNewFragment(), false);
    }

    private void firstStart() {
        fragmentController.loadEmptyFragment();
        fragmentController.loadConversationListFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleLaunch(null, intent, preferencesStore);
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
        if (!preferencesStore.isNotificationPersistent()) {
            SingletonManager.getNotifier(this).clearNewMessagesNotification();
        }
        appChecker.checkSmsApp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuController.create(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuController.prepare(menu, isSecondaryVisible);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menuController.itemSelected(item.getItemId()) ||
                fragmentController.optionsItemSelected(item) ||
                super.onOptionsItemSelected(item);
    }

    @Override
    public void showSettings() {
        statReporter.sendUiEvent("settings");
        fragmentController.showSettings();
    }

    @Override
    public void showAbout() {
        fragmentController.replaceFragment(new AboutFragment(), false);
    }

    @Override
    public void showConversationList() {
        statReporter.sendUiEvent("home_button");
        viewController.hideSecondary();
    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return messagesLoader;
    }

    @Override
    public void onBackPressed() {
        if (!viewController.backPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConversationSelected(Conversation conversation) {
        String address = conversation.getAddress();
        final ThreadFragment fragment = ThreadFragment.create(conversation.getThreadId(), address);

        fragmentController.replaceFragment(fragment, false);
        messagesLoader.queryContact(address, new OnContactQueryListener() {
            @Override
            public void contactLoaded(final Contact contact) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment.setContactView(contact, messagesLoader);
                    }
                });
            }
        });
    }

    @Override
    public void sendSms(InFlightSmsMessage smsMessage) {
        activityController.sendSms(smsMessage);
    }

    @Override
    public void callNumber(PhoneNumber phoneNumber) {
        statReporter.sendUiEvent("call_number");
        activityController.callNumber(phoneNumber);
    }

    @Override
    public void isDefaultSmsApp() {
        viewController.hideDisabledBanner();
        viewController.enableNewMessageButton();
    }

    @Override
    public void isNotDefaultSmsApp() {
        viewController.showDisabledBanner();
        viewController.disableNewMessageButton();
    }

    @Override
    public void secondaryVisible() {
        isSecondaryVisible = true;
        menuController.update();
        actionBarController.showHeader();
    }

    @Override
    public void secondaryHidden() {
        isSecondaryVisible = false;
        menuController.update();
        actionBarController.hideHeader();
    }

    @Override
    public void secondarySliding(float slideOffset) {
        //actionBarController.secondaryVisibility(slideOffset);
    }

    @Override
    public void defaultsBannerPressed() {
        activityController.switchSmsApp();
    }

    @Override
    public void newMessageButtonClicked() {
        fragmentController.loadComposeNewFragment();
    }

    @Override
    public void viewContact(String address) {
        statReporter.sendUiEvent("view_contact");
        messagesLoader.queryContact(address, new OnContactQueryListener() {

            @Override
            public void contactLoaded(Contact contact) {
                activityController.viewContact(ContactFactory.uriForContact(contact, getContentResolver()));
            }
        });
    }

    @Override
    public void deleteThreads(final List<Conversation> conversationList) {
        Dialog.Button no = new Dialog.Button("No");
        Dialog.Button yes = new Dialog.Button("Yes");
        blockingInUiNotifier.show(new BlockingInUiNotifier.Callbacks() {
            @Override
            public void positive() {
                messagesLoader.deleteThreads(conversationList, MessagesActivity.this);
            }

            @Override
            public void negative() {

            }
        }, getString(R.string.dialog_title_delete_threads), getString(R.string.dialog_sum_delete_threads), no, yes);
    }

    @Override
    public void markAsUnread(List<Conversation> threadId) {
        statReporter.sendUiEvent("mark_thread_unread");
        messagesLoader.markThreadAsUnread(threadId, this);
    }

    @Override
    public void threadDeleted(final List<Conversation> deletedConversations) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String toast;
                if (deletedConversations.size() == 1) {
                    toast = getString(R.string.deleted_one_thread, deletedConversations.get(0).getContact().getDisplayName());
                } else {
                    toast = getString(R.string.deleted_many_threads, deletedConversations.size());
                }
                toastInUiNotifier.notify(toast);
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

    @Override
    public void removeCustomHeader() {
        actionBarController.removeHeader();
    }

    @Override
    public void addCustomHeader(View headerView) {
        actionBarController.addHeader(headerView);
    }

}

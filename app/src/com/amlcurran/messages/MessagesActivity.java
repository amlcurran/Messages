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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.conversationlist.ConversationModalMarshall;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.core.loaders.ConversationListChangeListener;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.events.EventBus;
import com.amlcurran.messages.launch.IntentDataExtractor;
import com.amlcurran.messages.launch.LaunchAction;
import com.amlcurran.messages.launch.LaunchAssistant;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.loaders.OnThreadDeleteListener;
import com.amlcurran.messages.notifications.BlockingInUiDialogNotifier;
import com.amlcurran.messages.notifications.BlockingInUiNotifier;
import com.amlcurran.messages.notifications.Dialog;
import com.amlcurran.messages.notifications.InUiNotifier;
import com.amlcurran.messages.notifications.InUiToastNotifier;
import com.amlcurran.messages.preferences.PreferenceStore;
import com.amlcurran.messages.reporting.StatReporter;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.telephony.SmsSender;
import com.amlcurran.messages.transition.TransitionManager;
import com.amlcurran.messages.ui.NewMessageButtonController;
import com.amlcurran.messages.ui.actionbar.ActionBarHeaderCallback;
import com.amlcurran.messages.ui.actionbar.HoloActionBarController;
import com.amlcurran.messages.ui.control.FragmentController;
import com.amlcurran.messages.ui.control.SinglePaneFullScreenFragmentViewController;

import java.util.List;

public class MessagesActivity extends Activity implements MessagesLoaderProvider,
        ConversationListFragment.Listener, SmsComposeListener, ConversationModalMarshall.Callback,
        OnThreadDeleteListener, ConversationListChangeListener, FragmentController.FragmentCallback, MenuController.Callbacks {

    private InUiNotifier toastInUiNotifier;
    private StatReporter statReporter;
    private MenuController menuController;
    private DefaultAppChecker appChecker;
    private EventBus eventBus;
    private LaunchAssistant launchHelper = new LaunchAssistant();
    private boolean isSecondaryVisible;
    private MessagesLoader messagesLoader;
    private BlockingInUiNotifier blockingInUiNotifier;
    private PreferenceStore preferencesStore;
    private HoloActionBarController actionBarController;
    private DisabledBannerController disabledBannerController;
    private NewMessageButtonController newComposeController;
    private TransitionManager transitionManager;
    private ExternalEventManager externalEventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBarController = new HoloActionBarController(getActionBar());
        toastInUiNotifier = new InUiToastNotifier(this);
        blockingInUiNotifier = new BlockingInUiDialogNotifier(getFragmentManager());
        messagesLoader = SingletonManager.getMessagesLoader(this);
        statReporter = SingletonManager.getStatsReporter(this);
        eventBus = SingletonManager.getEventBus(this);
        preferencesStore = new PreferenceStore(this);

        FragmentController fragmentController = new SinglePaneFullScreenFragmentViewController(this, this, new ActionBarHeaderCallback(actionBarController));
        ActivityController activityController = new ActivityController(this, blockingInUiNotifier);
        transitionManager = new TransitionManager(fragmentController, activityController);
        externalEventManager = new ExternalEventManager(activityController, getContentResolver());

        setContentView(transitionManager.getView());

        menuController = new MenuController(this, this);
        disabledBannerController = new DisabledBannerController(this, externalEventManager);
        newComposeController = new NewMessageButtonController(findViewById(R.id.button_new_message), transitionManager);
        appChecker = new DefaultAppChecker(this, new HideNewComposeAndShowBannerCallback(newComposeController, disabledBannerController));

        handleLaunch(savedInstanceState, getIntent(), preferencesStore);
    }

    private void handleLaunch(Bundle savedInstanceState, Intent intent, PreferenceStore preferencesStore) {
        LaunchAction launchAction = launchHelper.getLaunchType(savedInstanceState, intent, preferencesStore);
        IntentDataExtractor intentDataExtractor = new IntentDataExtractor(intent);
        transitionManager.startAt().conversationList();
        launchAction.perform(transitionManager, intentDataExtractor);

        if (launchHelper.isFirstEverStart(preferencesStore)) {
            preferencesStore.storeHasShownAlphaMessage();
            statReporter.sendEvent("first_ever_start");
            showFirstDialog();
        } else {
            statReporter.sendEvent("not_new_start");
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
                super.onOptionsItemSelected(item);
    }

    @Override
    public void showSettings() {
        statReporter.sendUiEvent("settings");
        transitionManager.toPreferences();
    }

    @Override
    public void showAbout() {
        statReporter.sendUiEvent("about");
        transitionManager.toAbout();
    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return messagesLoader;
    }

    @Override
    public void onBackPressed() {
        if (!transitionManager.backPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConversationSelected(Conversation conversation) {
        transitionManager.to().thread(conversation.getContact(), conversation.getThreadId(), null);
    }

    @Override
    public void sendSms(InFlightSmsMessage smsMessage) {
        startService(SmsSender.sendMessageIntent(this, smsMessage));
    }

    @Override
    public void callNumber(PhoneNumber phoneNumber) {
        statReporter.sendUiEvent("call_number");
        externalEventManager.callNumber(phoneNumber);
    }

    @Override
    public void displayThread(Contact contact, int threadId, String writtenMessage) {
        transitionManager.to().thread(contact, String.valueOf(threadId), writtenMessage);
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
    public void viewContact(Contact contact) {
        statReporter.sendUiEvent("view_contact");
        externalEventManager.viewContact(contact);
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
    public void addContact(Contact contact) {
        externalEventManager.addContact(contact);
    }

    @Override
    public void threadDeleted(final List<Conversation> conversations) {
        toastInUiNotifier.deletedConversations(conversations);
        eventBus.postListInvalidated();
    }

    @Override
    public void listChanged() {
        eventBus.postListInvalidated();
    }

    @Override
    public void insertedDetail() {
        newComposeController.hideNewMessageButton();
    }

    @Override
    public void insertedMaster() {
        newComposeController.showNewMessageButton();
    }

}

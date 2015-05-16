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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.amlcurran.messages.conversationlist.DeleteThreadViewCallback;
import com.amlcurran.messages.core.conversationlist.ConversationLoader;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.events.EventBus;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.launch.IntentDataExtractor;
import com.amlcurran.messages.launch.LaunchAction;
import com.amlcurran.messages.launch.LaunchAssistant;
import com.amlcurran.messages.notifications.BlockingInUiDialogNotifier;
import com.amlcurran.messages.notifications.BlockingInUiNotifier;
import com.amlcurran.messages.notifications.Dialog;
import com.amlcurran.messages.preferences.PreferenceStoreDraftRepository;
import com.amlcurran.messages.preferences.SharedPreferenceStore;
import com.amlcurran.messages.reporting.StatReporter;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.telephony.SmsManagerOutputPort;
import com.amlcurran.messages.transition.TransitionManager;
import com.amlcurran.messages.ui.NewMessageButtonController;
import com.amlcurran.messages.ui.actionbar.ActionBarHeaderCallback;
import com.amlcurran.messages.ui.actionbar.HoloActionBarController;
import com.amlcurran.messages.ui.control.FragmentController;
import com.amlcurran.messages.ui.control.TwoPaneFullScreenFragmentViewController;

import java.util.List;

public class MessagesActivity extends ActionBarActivity implements
        SmsComposeListener, FragmentController.FragmentCallback,
        TransitionManager.Provider, DependencyRepository, DeleteThreadViewCallback {

    private StatReporter statReporter;
    private MenuController menuController;
    private DefaultAppChecker appChecker;
    private EventBus eventBus;
    private LaunchAssistant launchHelper = new LaunchAssistant();
    private boolean isSecondaryVisible;
    private MessagesLoader messagesLoader;
    private ConversationLoader conversationLoader;
    private BlockingInUiNotifier blockingInUiNotifier;
    private PreferenceStore preferencesStore;
    private HoloActionBarController actionBarController;
    private DisabledBannerController disabledBannerController;
    private NewMessageButtonController newComposeController;
    private TransitionManager transitionManager;
    private ExternalEventManager externalEventManager;
    private DraftRepository draftRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBarController = new HoloActionBarController(getSupportActionBar());
        blockingInUiNotifier = new BlockingInUiDialogNotifier(getFragmentManager());
        messagesLoader = SingletonManager.getMessagesLoader(this);
        conversationLoader = SingletonManager.getConversationLoader(this);
        preferencesStore = new SharedPreferenceStore(this);
        statReporter = SingletonManager.getStatReporter(this);
        eventBus = SingletonManager.getEventBus(this);
        draftRepository = new PreferenceStoreDraftRepository(this);

        FragmentController fragmentController = new TwoPaneFullScreenFragmentViewController(this, this, new ActionBarHeaderCallback(actionBarController));
        ActivityController activityController = new ActivityController(this, blockingInUiNotifier);
        transitionManager = new TransitionManager(fragmentController, activityController, statReporter);
        externalEventManager = new ExternalEventManager(activityController, getContentResolver(), statReporter);

        setContentView(transitionManager.getView());

        menuController = new MenuController(this, transitionManager);
        disabledBannerController = new DisabledBannerController(this, externalEventManager);
        newComposeController = new NewMessageButtonController(findViewById(R.id.button_new_message), transitionManager, statReporter);
        appChecker = new DefaultAppChecker(this);

        handleLaunch(getIntent(), preferencesStore);
    }

    private void handleLaunch(Intent intent, PreferenceStore preferencesStore) {
        LaunchAction launchAction = launchHelper.getLaunchType(intent);
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
        handleLaunch(intent, preferencesStore);
    }

    @Override
    protected void onStart() {
        super.onStart();
        statReporter.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        statReporter.stop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appChecker.checkSmsApp(new HideNewComposeAndShowBannerCallback(newComposeController, disabledBannerController));
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
    public void sendSms(InFlightSmsMessage smsMessage) {
        startService(SmsManagerOutputPort.sendMessageIntent(this, null, smsMessage));
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
    public void deleteThreads(final List<Conversation> conversationList) {
        statReporter.sendUiEvent("delete_threads");
        Dialog.Button no = new Dialog.Button("No");
        Dialog.Button yes = new Dialog.Button("Yes");
        blockingInUiNotifier.show(new BlockingInUiNotifier.Callbacks() {
            @Override
            public void positive() {
                conversationLoader.deleteConversations(conversationList);
            }

            @Override
            public void negative() {

            }
        }, getString(R.string.dialog_title_delete_threads), getString(R.string.dialog_sum_delete_threads), no, yes);
    }

    @Override
    public void insertedDetail() {
        newComposeController.hideNewMessageButton();
    }

    @Override
    public void insertedMaster() {
        newComposeController.showNewMessageButton();
    }

    @Override
    public TransitionManager getTransitionManager() {
        return transitionManager;
    }

    @Override
    public ExternalEventManager getExternalEventManager() {
        return externalEventManager;
    }

    @Override
    public PreferenceStore getPreferenceStore() {
        return preferencesStore;
    }

    @Override
    public DraftRepository getDraftRepository() {
        return draftRepository;
    }
}

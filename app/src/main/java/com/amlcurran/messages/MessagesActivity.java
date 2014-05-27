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
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.conversationlist.ConversationModalMarshall;
import com.amlcurran.messages.data.Conversation;
import com.amlcurran.messages.data.SmsMessage;
import com.amlcurran.messages.events.BroadcastEventBus;
import com.amlcurran.messages.loaders.ConversationListChangeListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.loaders.OnContactQueryListener;
import com.amlcurran.messages.loaders.OnThreadDeleteListener;
import com.amlcurran.messages.reporting.EasyTrackerStatReporter;
import com.amlcurran.messages.reporting.NullStatReporter;
import com.amlcurran.messages.reporting.StatReporter;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.threads.ThreadFragment;
import com.amlcurran.messages.ui.SlidingPaneFragmentController;
import com.amlcurran.messages.ui.UiController;
import com.espian.utils.ui.MenuFinder;
import com.google.analytics.tracking.android.EasyTracker;

import java.util.Calendar;
import java.util.List;

public class MessagesActivity extends Activity implements MessagesLoaderProvider,
        ConversationListFragment.Listener, ThreadFragment.Listener, View.OnClickListener,
        DefaultAppChecker.Callback, SlidingPaneFragmentController.UiCallback, ConversationModalMarshall.Callback, OnThreadDeleteListener, ConversationListChangeListener {

    private StatReporter statReporter;
    private UiController fragmentController;
    private DefaultAppChecker appChecker;
    private BroadcastEventBus eventBus;
    private ActivityController activityController;
    private boolean isSecondaryVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentController = new SlidingPaneFragmentController(this, this);
        activityController = new ActivityController(this);
        setContentView(fragmentController.getView());

        statReporter = new EasyTrackerStatReporter(EasyTracker.getInstance(this));
        appChecker = new DefaultAppChecker(this, this);
        eventBus = new BroadcastEventBus(this);
        fragmentController.getDisabledBanner().setOnClickListener(this);

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .build());
            statReporter = new NullStatReporter();
        }

        if (savedInstanceState == null) {
            fragmentController.loadMessagesListFragment();
            fragmentController.loadEmptyFragment();
        }

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
        getMenuInflater().inflate(R.menu.activity_messages, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int[] menuResList = new int[] { R.id.menu_call };
        for (int menuRes : menuResList) {
            MenuItem item = MenuFinder.findItemById(menu, menuRes);
            item.setVisible(isSecondaryVisible);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                statReporter.sendUiEvent("settings");
                fragmentController.showSettings();
                return true;

            case R.id.action_about:
                activityController.showAbout();
                return true;

            case android.R.id.home:
                statReporter.sendUiEvent("home_button");
                fragmentController.hideSecondary();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return MessagesApp.getMessagesLoader(this);
    }

    @Override
    public void onBackPressed() {
        if (!fragmentController.backPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConversationSelected(Conversation conversation) {
        ThreadFragment fragment = ThreadFragment.create(conversation.getThreadId(), conversation.getAddress());
        fragmentController.replaceFragment(fragment, false);
    }

    @Override
    public void sendSms(String address, String body) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        SmsMessage message = new SmsMessage(address, body, timestamp, true, false);
        activityController.sendSms(message);
    }

    @Override
    public void callNumber(String sendAddress) {
        statReporter.sendUiEvent("call_number");
        activityController.callNumber(sendAddress);
    }

    @Override
    public void onClick(View v) {
        activityController.switchSmsApp();
    }

    @Override
    public void isDefaultSmsApp() {
        fragmentController.hideDisabledBanner();
    }

    @Override
    public void isNotDefaultSmsApp() {
        fragmentController.showDisabledBanner();
    }

    @Override
    public void onSecondaryVisible() {
        isSecondaryVisible = true;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        invalidateOptionsMenu();
    }

    @Override
    public void onSecondaryHidden() {
        isSecondaryVisible = false;
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(false);
        invalidateOptionsMenu();
    }

    @Override
    public void viewContact(String address) {
        statReporter.sendUiEvent("view_contact");
        getMessagesLoader().queryContact(address, new OnContactQueryListener() {

            @Override
            public void contactLoaded(Uri contactUri) {
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
                String toast;
                if (deletedConversations.size() == 1) {
                    toast = getString(R.string.deleted_one_thread, deletedConversations.get(0).getContact().getDisplayName());
                } else {
                    toast = getString(R.string.deleted_many_threads, deletedConversations.size());
                }
                Toast.makeText(MessagesActivity.this, toast, Toast.LENGTH_SHORT).show();
                eventBus.postListChanged();
            }
        });
    }

    @Override
    public void listChanged() {
        eventBus.postListChanged();
    }

    public static class EmptyFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_empty, container, false);
        }
    }
}

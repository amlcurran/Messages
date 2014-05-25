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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.data.Conversation;
import com.amlcurran.messages.data.SmsMessage;
import com.amlcurran.messages.events.BroadcastManagerEventBus;
import com.amlcurran.messages.loaders.ConversationListChangeListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.loaders.OnContactQueryListener;
import com.amlcurran.messages.loaders.OnThreadDeleteListener;
import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.telephony.SmsSender;
import com.amlcurran.messages.ui.SlidingPaneUiController;
import com.amlcurran.messages.ui.UiController;
import com.espian.utils.ui.MenuFinder;

import java.util.Calendar;
import java.util.List;

public class MessagesActivity extends Activity implements MessagesLoaderProvider,
        ConversationListFragment.Listener, ThreadFragment.Listener, View.OnClickListener,
        DefaultAppChecker.Callback, SlidingPaneUiController.UiCallback, ConversationModalMarshall.Callback, OnThreadDeleteListener, ConversationListChangeListener {

    public static final int REQUEST_CHANGE_SMS_APP = 20;

    private UiController uiController;
    private DefaultAppChecker appChecker;
    private boolean isSecondaryVisible;
    private BroadcastManagerEventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiController = new SlidingPaneUiController(this, this);
        setContentView(uiController.getView());

        appChecker = new DefaultAppChecker(this, this);
        eventBus = new BroadcastManagerEventBus(this);
        uiController.getDisabledBanner().setOnClickListener(this);

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .build());
        }

        if (savedInstanceState == null) {
            uiController.loadMessagesListFragment();
            uiController.loadEmptyFragment();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MessagesApp.getNotifier(this).clearNewMessagesNotification();
        appChecker.checkSmsApp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages, menu);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MessagesActivity", String.format("%1$s, %2$s, %3$s", requestCode, resultCode, String.valueOf(data)));
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return MessagesApp.getMessagesLoader(this);
    }

    @Override
    public void onBackPressed() {
        if (!uiController.backPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConversationSelected(Conversation conversation) {
        ThreadFragment fragment = ThreadFragment.create(conversation.getThreadId(), conversation.getAddress());
        uiController.replaceFragment(fragment);
    }

    @Override
    public void sendSms(String address, String body) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        SmsMessage message = new SmsMessage(address, body, timestamp, true);
        Intent intent = new Intent(this, SmsSender.class);
        intent.setAction(SmsSender.ACTION_SEND_REQUEST);
        intent.putExtra(SmsSender.EXTRA_MESSAGE, message);
        startService(intent);
    }

    @Override
    public void callNumber(String sendAddress) {
        Uri telUri = Uri.parse("tel:" + PhoneNumberUtils.stripSeparators(sendAddress));
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(telUri);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, MessagesActivity.REQUEST_CHANGE_SMS_APP);
    }

    @Override
    public void isDefaultSmsApp() {
        uiController.hideDisabledBanner();
    }

    @Override
    public void isNotDefaultSmsApp() {
        uiController.showDisabledBanner();
    }

    @Override
    public void onSecondaryVisible() {
        isSecondaryVisible = true;
        invalidateOptionsMenu();
    }

    @Override
    public void onSecondaryHidden() {
        isSecondaryVisible = false;
        invalidateOptionsMenu();
    }

    @Override
    public void viewContact(String address) {
        getMessagesLoader().queryContact(address, new OnContactQueryListener() {

            @Override
            public void contactLoaded(Uri contactUri) {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                viewIntent.setData(contactUri);
                startActivity(viewIntent);
            }
        });
    }

    @Override
    public void deleteThreads(List<Conversation> conversationList) {
        getMessagesLoader().deleteThreads(conversationList, this);
    }

    @Override
    public void markAsUnread(List<Conversation> threadId) {
        getMessagesLoader().markThreadAsUnread(threadId, this);
    }

    @Override
     public void threadDeleted(final List<Conversation> deletedConversations) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String toast;
                if (deletedConversations.size() == 1) {
                    toast = getString(R.string.deleted_one_thread, deletedConversations.get(0).getName());
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

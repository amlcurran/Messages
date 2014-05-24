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
import android.support.v4.content.LocalBroadcastManager;
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
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.loaders.OnThreadDeleteListener;
import com.amlcurran.messages.ui.SlidingPaneUiController;
import com.amlcurran.messages.ui.UiController;
import com.espian.utils.MenuFinder;

import java.util.Calendar;

public class MessagesActivity extends Activity implements MessagesLoaderProvider,
        ConversationListFragment.Listener, ThreadFragment.Listener, View.OnClickListener,
        DefaultAppChecker.Callback, SlidingPaneUiController.UiCallback, ConversationModalMarshall.Callback, OnThreadDeleteListener {

    public static final int REQUEST_CHANGE_SMS_APP = 20;

    private UiController uiController;
    private DefaultAppChecker appChecker;
    private boolean isSecondaryVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiController = new SlidingPaneUiController(this, this);
        setContentView(uiController.getView());

        appChecker = new DefaultAppChecker(this, this);
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
    public void onConversationModalSelected(Conversation conversation) {
        startActionMode(new ConversationModalMarshall(conversation, this));
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
    public void deleteThread(Conversation conversation) {
        getMessagesLoader().deleteThread(conversation, this);
    }

    @Override
     public void threadDeleted(final Conversation conversation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String toast = String.format("Deleted thread from %1$s", conversation.getName());
                Toast.makeText(MessagesActivity.this, toast, Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(MessagesActivity.this).sendBroadcast(new Intent(SmsSender.BROADCAST_MESSAGE_SENT));
            }
        });
    }

    public static class EmptyFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_empty, container, false);
        }
    }
}

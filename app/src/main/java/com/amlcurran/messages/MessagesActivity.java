package com.amlcurran.messages;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.amlcurran.messages.conversationlist.Conversation;
import com.amlcurran.messages.conversationlist.ConversationListFragment;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.amlcurran.messages.threads.ThreadFragment;
import com.amlcurran.messages.ui.SlidingPaneUiController;
import com.amlcurran.messages.ui.UiController;

import java.util.Calendar;

public class MessagesActivity extends Activity implements MessagesLoaderProvider,
        ConversationListFragment.Listener, ThreadFragment.Listener, View.OnClickListener,
        DefaultAppChecker.Callback {

    public static final int REQUEST_CHANGE_SMS_APP = 20;

    private UiController uiController;
    private Notifier notifier;
    private DefaultAppChecker appChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiController = new SlidingPaneUiController(this);
        setContentView(uiController.getView());

        notifier = new Notifier(this);
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
    protected void onStart() {
        super.onStart();
        notifier.clearNewMessagesNotification();
        appChecker.checkSmsApp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        appChecker.checkSmsApp();
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
    public void onSendMessage(String address, String body) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        SmsMessage message = new SmsMessage(address, body, timestamp);
        Intent intent = new Intent(this, SmsSender.class);
        intent.setAction(SmsSender.ACTION_SEND_REQUEST);
        intent.putExtra(SmsSender.EXTRA_MESSAGE, message);
        startService(intent);
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

    public static class EmptyFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_empty, container, false);
        }
    }
}

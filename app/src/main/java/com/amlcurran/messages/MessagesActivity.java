package com.amlcurran.messages;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.SmsManager;
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

import java.util.ArrayList;
import java.util.concurrent.Executors;


public class MessagesActivity extends Activity implements MessagesLoaderProvider,
        ConversationListFragment.Listener, ThreadFragment.Listener {

    private static final int CODE_SENT_MESSAGE = 3030;

    private final MessagesLoader messagesLoader = new MessagesLoader(this, Executors.newCachedThreadPool());
    private UiController uiController;
    private SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiController = new SlidingPaneUiController(this);
        smsManager = SmsManager.getDefault();

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .build());
        }

        setContentView(uiController.getView());

        if (savedInstanceState == null) {
            uiController.loadMessagesListFragment();
            uiController.loadEmptyFragment();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MessagesActivity", String.format("%1$s, %2$s, %3$s", requestCode, resultCode, data.toString()));
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return messagesLoader;
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
    public void onSendMessage(String address, String message) {
        Intent intent = new Intent(this, MessagesActivity.class);
        PendingIntent sentIntent = PendingIntent.getActivity(this, CODE_SENT_MESSAGE, intent, 0);
        ArrayList<PendingIntent> sendIntents = new ArrayList<PendingIntent>();
        sendIntents.add(sentIntent);
        smsManager.sendMultipartTextMessage(address, null, smsManager.divideMessage(message), sendIntents, null);
    }

    public static class EmptyFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_empty, container, false);
        }
    }
}

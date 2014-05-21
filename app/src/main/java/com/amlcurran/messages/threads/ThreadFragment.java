package com.amlcurran.messages.threads;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amlcurran.messages.DefaultAppChecker;
import com.amlcurran.messages.ListeningCursorListFragment;
import com.espian.utils.ProviderHelper;
import com.amlcurran.messages.R;
import com.amlcurran.messages.adapters.AdaptiveCursorSource;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.espian.utils.SimpleBinder;
import com.espian.utils.SourceBinderAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadFragment extends ListeningCursorListFragment<ThreadMessage> implements View.OnClickListener, TextWatcher, DefaultAppChecker.Callback {

    private static final String THREAD_ID = "threadId";
    private static final String ADDRESS = "address";

    private EditText smsEntryField;
    private Listener listener;
    private String sendAddress;
    private DefaultAppChecker defaultChecker;
    private ImageButton sendButton;

    public static ThreadFragment create(String threadId, String address) {
        Bundle bundle = new Bundle();
        bundle.putString(THREAD_ID, threadId);
        bundle.putString(ADDRESS, address);

        ThreadFragment fragment = new ThreadFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thread, container, false);

        smsEntryField = (EditText) view.findViewById(R.id.thread_sms_entry);
        smsEntryField.addTextChangedListener(this);
        sendButton = (ImageButton) view.findViewById(R.id.thread_sms_send);
        sendButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = new ProviderHelper<Listener>(Listener.class).get(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sendAddress = getArguments().getString(ADDRESS);
        source = new ThreadMessageAdaptiveCursorSource();
        adapter = new SourceBinderAdapter<ThreadMessage>(getActivity(), source, new ThreadBinder());
        defaultChecker = new DefaultAppChecker(getActivity(), this);
        setListAdapter(adapter);
        getListView().setStackFromBottom(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        defaultChecker.checkSmsApp();
    }

    @Override
    public void loadData(MessagesLoader loader) {
        loader.loadThread(getThreadId(), this);
    }

    private String getThreadId() {
        return getArguments().getString(THREAD_ID);
    }

    @Override
    public void onCursorLoaded(Cursor cursor) {
        super.onCursorLoaded(cursor);
        getMessageLoader().markThreadAsRead(getThreadId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.thread_sms_send:
                sendMessage();
                break;

        }
    }

    private void sendMessage() {
        if (!TextUtils.isEmpty(smsEntryField.getText())) {
            listener.onSendMessage(sendAddress, smsEntryField.getText().toString());
            clearSmsEntryField();
        }
    }

    private void clearSmsEntryField() {
        smsEntryField.setText("", TextView.BufferType.EDITABLE);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void isDefaultSmsApp() {
        smsEntryField.setEnabled(true);
        smsEntryField.setHint(R.string.hint_send_message);
        sendButton.setEnabled(true);
        sendButton.setImageResource(R.drawable.ic_action_send);
    }

    @Override
    public void isNotDefaultSmsApp() {
        smsEntryField.setEnabled(false);
        smsEntryField.setHint(R.string.hint_send_message_disabled);
        sendButton.setEnabled(false);
        sendButton.setImageResource(R.drawable.ic_action_send_disabled);
    }

    private class ThreadBinder extends SimpleBinder<ThreadMessage> {

        private static final int ITEM_ME = 0;
        private static final int ITEM_THEM = 1;
        private final DateFormat formatter = new SimpleDateFormat("HH:mm dd-MMM-yy");
        private final Date date = new Date();

        @Override
        public View bindView(View convertView, ThreadMessage item, int position) {

            date.setTime(item.getTimestamp());

            TextView bodyText = getTextView(convertView, android.R.id.text1);
            bodyText.setText(item.getBody());
            getTextView(convertView, android.R.id.text2).setText(formatter.format(date));
            Linkify.addLinks(bodyText, Linkify.ALL);

            return convertView;
        }

        @Override
        public View createView(Context context, int itemViewType) {
            if (itemViewType == ITEM_ME) {
                return LayoutInflater.from(context).inflate(R.layout.item_thread_item_me, getListView(), false);
            } else if (itemViewType == ITEM_THEM) {
                return LayoutInflater.from(context).inflate(R.layout.item_thread_item_them, getListView(), false);
            }
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position, ThreadMessage item) {
            return item.isFromMe() ? ITEM_ME : ITEM_THEM;
        }

        private TextView getTextView(View convertView, int text1) {
            return (TextView) convertView.findViewById(text1);
        }

    }

    private static class ThreadMessageAdaptiveCursorSource extends AdaptiveCursorSource<ThreadMessage> {

        @Override
        public ThreadMessage getFromCursorRow(Cursor cursor) {
            return ThreadMessage.fromCursor(cursor);
        }
    }

    public interface Listener {
        void onSendMessage(String address, String message);
    }

}

package com.amlcurran.messages.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.espian.utils.SimpleBinder;

public class AllMessagesBinder extends SimpleBinder<Cursor> {

    @Override
    public View bindView(View convertView, Cursor item, int position) {
        String person = CursorHelper.fromColumn(item, Telephony.Sms.Inbox.PERSON);
        String body = CursorHelper.fromColumn(item, Telephony.Sms.Inbox.BODY);
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(person);
        ((TextView) convertView.findViewById(android.R.id.text2)).setText(body);

        if (isNotRead(item)) {
            setUnreadStyle(convertView);
        } else {
            setReadStyle(convertView);
        }

        return convertView;
    }

    private void setReadStyle(View convertView) {
        ((TextView) convertView.findViewById(android.R.id.text2)).setTypeface(null, 0);
    }

    private void setUnreadStyle(View convertView) {
        ((TextView) convertView.findViewById(android.R.id.text2)).setTypeface(null, Typeface.BOLD_ITALIC);
    }

    private boolean isNotRead(Cursor item) {
        String s = CursorHelper.fromColumn(item, Telephony.Sms.Inbox.READ);
        return s.toLowerCase().equals("0");
    }

    @Override
    public View createView(Context context) {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, null);
    }
}

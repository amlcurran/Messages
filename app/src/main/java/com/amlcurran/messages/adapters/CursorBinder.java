package com.amlcurran.messages.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.espian.utils.Binder;

public class CursorBinder implements Binder<Cursor> {

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getSourcePosition(int position) {
        return position;
    }

    @Override
    public View bindView(View convertView, Cursor item, int position) {
        String person = CursorHelper.fromColumn(item, Telephony.Sms.Inbox.PERSON);
        String body = CursorHelper.fromColumn(item, Telephony.Sms.Inbox.BODY);
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(person);
        ((TextView) convertView.findViewById(android.R.id.text2)).setText(body);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View createView(Context context) {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, null);
    }
}

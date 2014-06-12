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

package com.amlcurran.messages.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.MessagesLoader;

public class CustomActionBarView extends LinearLayout {

    public static final String TAG = CustomActionBarView.class.getSimpleName();

    private final LinearLayout actionItemsHost;
    private final View homeChip;
    private final ContactView contactChip;
    private OnOptionsItemSelectedListener listener = OnOptionsItemSelectedListener.NONE;
    private Menu menu;

    public CustomActionBarView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.ActionBar_Solid_Messages);
    }

    public CustomActionBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, R.style.ActionBar_Solid_Messages);

        LayoutInflater.from(context).inflate(R.layout.view_action_bar, this, true);

        actionItemsHost = (LinearLayout) findViewById(R.id.action_bar_items);
        homeChip = findViewById(R.id.home_chip);
        contactChip = ((ContactView) findViewById(R.id.contact_chip));

        style();
        init();
    }

    public void setSecondaryVisibility(float visibility) {
        contactChip.setAlpha(1 - visibility);
        homeChip.setAlpha(visibility);
    }

    public void setOnOptionsItemSelectedListener(OnOptionsItemSelectedListener listener) {
        this.listener = listener;
    }

    public void setMenu(Menu menu) {
        actionItemsHost.removeAllViews();
        LayoutParams layoutParams = createItemLayoutParams();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.isVisible()) {
                if (item.getOrder() != 100) {
                    View menuView = createMenuView(item);
                    actionItemsHost.addView(menuView, layoutParams);
                } else {
                    // Deal with items that shouldn't be shown as actions
                }
            }
        }
    }

    private LayoutParams createItemLayoutParams() {
        LayoutParams params = new LayoutParams(getMeasuredHeight(), ViewGroup.LayoutParams.MATCH_PARENT);
        return params;
    }

    private View createMenuView(MenuItem item) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(item.getIcon());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setBackgroundResource(R.drawable.selectable_background_messages);
        imageView.setOnClickListener(mActionItemClickListener);
        imageView.setOnLongClickListener(mActionItemLongClickListener);
        imageView.setTag(item);
        return imageView;
    }

    private void style() {
    }

    private void init() {

    }

    private OnClickListener mActionItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                listener.onOptionsItemSelected(((MenuItem) v.getTag()));
            } else {
                Log.w(TAG, "Action view clicked without menu item tag");
            }
        }
    };

    private OnLongClickListener mActionItemLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (v.getTag() != null) {
                MenuItem menuItem = ((MenuItem) v.getTag());
                Toast toast = Toast.makeText(getContext(), menuItem.getTitle(), Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Log.w(TAG, "Action view long clicked without menu item tag");
            }
            return true;
        }
    };

    public void selectedContact(Contact contact, MessagesLoader messagesLoader) {
        contactChip.setContact(contact, messagesLoader);
    }

    public void updateSelf() {
        setMenu(menu);
    }
}

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

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.amlcurran.messages.R;

import java.util.ArrayList;
import java.util.List;

public class CustomActionBarView extends LinearLayout {

    public static final String TAG = CustomActionBarView.class.getSimpleName();

    private final LinearLayout actionItemsHost;
    private final View homeChip;
    private final FrameLayout customChip;
    private final List<MenuItem> overflowItems = new ArrayList<MenuItem>();
    private OnOptionsItemSelectedListener listener = OnOptionsItemSelectedListener.NONE;
    private boolean hasCustomView;

    public CustomActionBarView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.ActionBar_Solid_Messages);
    }

    public CustomActionBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, R.style.ActionBar_Solid_Messages);

        LayoutInflater.from(context).inflate(R.layout.view_action_bar, this, true);

        actionItemsHost = (LinearLayout) findViewById(R.id.action_bar_items);
        actionItemsHost.getLayoutTransition().setDuration(300);
        actionItemsHost.getLayoutTransition().setStartDelay(LayoutTransition.APPEARING, 0);
        actionItemsHost.getLayoutTransition().setAnimator(LayoutTransition.CHANGE_APPEARING, null);
        actionItemsHost.getLayoutTransition().setAnimator(LayoutTransition.CHANGE_DISAPPEARING, null);
        actionItemsHost.getLayoutTransition().setAnimator(LayoutTransition.DISAPPEARING, null);
        homeChip = findViewById(R.id.home_chip);
        customChip = ((FrameLayout) findViewById(R.id.contact_chip));
        customChip.setAlpha(0f);

        style();
        init();
    }

    public void setSecondaryVisibility(float visibility) {
        if (hasCustomView) {
            customChip.setAlpha(1 - visibility);
            homeChip.setAlpha(visibility);
        }
    }

    public void setOnOptionsItemSelectedListener(OnOptionsItemSelectedListener listener) {
        this.listener = listener;
    }

    public void setMenu(Menu menu) {

        boolean hasOverflowView = clearActionBar();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.isVisible()) {
                if (item.getOrder() != 100) {
                    actionItemsHost.addView(createMenuView(item), 0, createItemLayoutParams());
                } else {
                    if (!hasOverflowView) {
                        actionItemsHost.addView(createOverflowView(), createItemLayoutParams());
                        hasOverflowView = true;
                    }
                    overflowItems.add(item);
                    // Deal with items that shouldn't be shown as actions
                }
            }
        }
    }

    private boolean clearActionBar() {
        boolean hasOverflowView = false;
        overflowItems.clear();

        List<View> viewsToRemove = new ArrayList<View>();
        for (int i = 0; i < actionItemsHost.getChildCount(); i++) {
            View actionItem = actionItemsHost.getChildAt(i);
            if (actionItem.getId() != R.id.action_overflow) {
                viewsToRemove.add(actionItem);
            } else {
                hasOverflowView = true;
            }
        }

        for (View view : viewsToRemove) {
            actionItemsHost.removeView(view);
        }
        return hasOverflowView;
    }

    private LayoutParams createItemLayoutParams() {
        return new LayoutParams(getMeasuredHeight(), ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private View createMenuView(MenuItem item) {
        Drawable itemIcon = item.getIcon();
        ImageView imageView = createMenuItem(itemIcon);
        imageView.setOnClickListener(mActionItemClickListener);
        imageView.setOnLongClickListener(mActionItemLongClickListener);
        imageView.setTag(item);
        return imageView;
    }

    private View createOverflowView() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_overflow_white_16);
        ImageView overflowItem = createMenuItem(drawable);
        overflowItem.setId(R.id.action_overflow);
        overflowItem.setOnClickListener(mOverflowItemClickListener);
        return overflowItem;
    }

    private ImageView createMenuItem(Drawable itemIcon) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(itemIcon);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setBackgroundResource(R.drawable.selectable_background_messages);
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

    private OnClickListener mOverflowItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu menu = new PopupMenu(ThemeHelper.getThemedContext(getContext()), v);
            for (MenuItem item : overflowItems) {
                menu.getMenu().add(0, item.getItemId(), 0, item.getTitle());
            }
            menu.setOnMenuItemClickListener(popUpSelectionListener);
            v.setOnTouchListener(menu.getDragToOpenListener());
            menu.show();
        }
    };

    private PopupMenu.OnMenuItemClickListener popUpSelectionListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return listener.onOptionsItemSelected(item);
        }
    };

    public void removeCustomHeader() {
        hasCustomView = false;
        customChip.removeAllViews();
        customChip.setVisibility(INVISIBLE);
        homeChip.setAlpha(1f);
    }

    public void addCustomHeader(View customHeader) {
        hasCustomView = true;
        customChip.removeAllViews();
        customChip.addView(customHeader);
        customChip.setVisibility(VISIBLE);
    }
}

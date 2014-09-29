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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amlcurran.messages.preferences.PreferencesFragment;

public class SecondaryActivity extends Activity {

    private static final int PREFERENCES = 0;
    private static final int ABOUT = 1;
    private static final String PAGE = "page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        Fragment fragment = getDisplayFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }

    private Fragment getDisplayFragment() {
        Fragment fragment;
        if (getIntent().getIntExtra(PAGE, PREFERENCES) == PREFERENCES) {
            fragment = new PreferencesFragment();
        } else {
            fragment = new AboutFragment();
        }
        return fragment;
    }

    public static Intent about(Context context) {
        Intent intent = new Intent(context, SecondaryActivity.class);
        intent.putExtra(PAGE, ABOUT);
        return intent;
    }

    public static Intent preferences(Context context) {
        Intent intent = new Intent(context, SecondaryActivity.class);
        intent.putExtra(PAGE, PREFERENCES);
        return intent;
    }

}

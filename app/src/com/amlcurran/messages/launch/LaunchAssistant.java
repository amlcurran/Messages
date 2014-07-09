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

package com.amlcurran.messages.launch;

import android.content.Intent;
import android.os.Bundle;

import com.amlcurran.messages.notifications.Notifier;
import com.amlcurran.messages.preferences.PreferenceStore;

public class LaunchAssistant {

    public static final String EXTRA_THREAD_ID = "thread_id";
    public static final String EXTRA_ADDRESS = "address";

    public Launch getLaunchType(Bundle savedInstanceState, Intent intent, PreferenceStore preferencesStore) {

        if (preferencesStore.hasNotShownAlphaMessage()) {
            preferencesStore.storeHasShownAlphaMessage();
            return Launch.SHOW_ALPHA_MESSAGEN;
        }

        if (schemeIs(intent, "sms")) {
            return Launch.SEND_ANONYMOUS;
        }

        if (schemeIs(intent, "smsto")) {
            return Launch.SEND_TO;
        }

        if (schemeIs(intent, "mms") || schemeIs(intent, "mmsto")) {
            return Launch.MMS_TO;
        }

        if (Notifier.ACTION_VIEW_CONVERSATION.equals(intent.getAction())) {
            return Launch.VIEW_CONVERSATION;
        }

        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            return Launch.SHARE_TO;
        }

        // if the saved instance state is null, its a first start
        if (savedInstanceState == null) {
            return Launch.FIRST_START;
        }

        return Launch.OTHER;
    }

    private static boolean schemeIs(Intent intent, String sms) {
        return intent.getData() != null && intent.getData().getScheme().equals(sms);
    }
}

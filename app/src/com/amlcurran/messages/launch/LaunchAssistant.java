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

import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.notifications.Notifier;

public class LaunchAssistant {

    public static final String EXTRA_THREAD_ID = "thread_id";
    public static final String EXTRA_ADDRESS = "address";

    public LaunchAction getLaunchType(Intent intent) {

        if (schemeIs(intent, "sms")) {
            return new AnonymousSendAction();
        }

        if (schemeIs(intent, "smsto")) {
            return new SendToAddressAction();
        }

        if (schemeIs(intent, "mms") || schemeIs(intent, "mmsto")) {
            return new MmsRedirectAction();
        }

        if (actionIs(intent, Notifier.ACTION_VIEW_CONVERSATION)) {
            return new ViewConversationAction();
        }

        if (actionIs(intent, Intent.ACTION_SEND)) {
            return new SmsMessageAction();
        }

        return new NoAction();
    }

    private static boolean actionIs(Intent intent, String action) {
        return action.equals(intent.getAction());
    }

    private static boolean schemeIs(Intent intent, String sms) {
        return intent.getData() != null && intent.getData().getScheme().equals(sms);
    }

    public boolean isFirstEverStart(PreferenceStore preferencesStore) {
        return preferencesStore.hasNotShownAlphaMessage();
    }
}

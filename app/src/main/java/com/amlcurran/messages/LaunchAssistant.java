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

import android.content.Intent;
import android.os.Bundle;

public class LaunchAssistant {

    public Launch getLaunchType(Bundle savedInstanceState, Intent intent) {

        if (schemeIs(intent, "sms")) {
            return Launch.SEND_ANONYMOUS;
        }

        if (schemeIs(intent, "smsto")) {
            return Launch.SEND_TO;
        }

        if (schemeIs(intent, "mms") || schemeIs(intent, "mmsto")) {
            throw new RuntimeException("MMS isn't currently supported");
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

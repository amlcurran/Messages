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
import android.net.Uri;

import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.data.ParcelablePhoneNumber;

public class IntentDataExtractor {
    private final Intent intent;

    public IntentDataExtractor(Intent intent) {
        this.intent = intent;
    }

    public String getAddressFromUri() {
        Uri data = intent.getData();
        return data.getSchemeSpecificPart();
    }

    public String getMessage() {
        return intent.getStringExtra(Intent.EXTRA_TEXT);
    }

    public String getThreadId() {
        return intent.getStringExtra(LaunchAssistant.EXTRA_THREAD_ID);
    }

    public PhoneNumber getAddress() {
        return new ParcelablePhoneNumber(intent.getStringExtra(LaunchAssistant.EXTRA_ADDRESS));
    }
}
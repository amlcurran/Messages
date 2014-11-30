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

package com.amlcurran.messages.threads;

import android.app.Activity;
import android.content.Intent;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.telephony.SmsAsyncService;
import com.amlcurran.messages.ui.ComposeMessageView;

class DeleteFailedResender implements ResendCallback {
    private Activity activity;
    private ComposeMessageView.ComposureCallbacks composureCallbacks;

    public DeleteFailedResender(Activity activity, ComposeMessageView.ComposureCallbacks composureCallbacks) {
        this.activity = activity;
        this.composureCallbacks = composureCallbacks;
    }

    @Override
    public void resend(SmsMessage message) {
        composureCallbacks.onMessageComposed(message.getBody());
        Intent deleteFailed = SmsAsyncService.getAsyncDeleteIntent(activity, message);
        activity.startService(deleteFailed);
    }
}

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

import android.util.Log;

import com.amlcurran.messages.core.Logger;
import com.amlcurran.messages.core.data.SmsMessage;

public class NewMessagesLogger implements Logger {

    @Override
    public void d(Object object, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(object.getClass().getSimpleName(), message);
        }
    }

    @Override
    public void d(Object object, String message, Object... objects) {
        d(object, String.format(message, objects));
    }

    @Override
    public void w(Object object, String message) {
        Log.w(object.getClass().getSimpleName(), message);
    }

    @Override
    public void e(Object object, Throwable throwable) {
        e(object, throwable.getLocalizedMessage());
    }

    @Override
    public void e(Object object, String message) {
        Log.e(object.getClass().getSimpleName(), message);
    }

    public String format(SmsMessage message) {
        if (BuildConfig.DEBUG) {
            return String.format("From: %1$s\nMessage: %2$s", message.getAddress(), message.getBody());
        } else {
            return "Message contents hidden for security.";
        }
    }

}

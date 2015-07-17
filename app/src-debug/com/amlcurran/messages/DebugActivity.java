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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.data.Time;
import com.amlcurran.messages.data.ParcelablePhoneNumber;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        bindButton(R.id.button_notify_single, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsMessage dummySms = new SmsMessage(12, "12", new ParcelablePhoneNumber("0800 121 121"),
                        "Here's the body of the message", Time.now(), SmsMessage.Type.INBOX);
                SingletonManager.getNotifier(DebugActivity.this).addNewMessageNotification(dummySms);
            }
        });
    }

    private void bindButton(int resId, View.OnClickListener l) {
        findViewById(resId).setOnClickListener(l);
    }
}

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

import com.amlcurran.messages.core.TextUtils;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.telephony.DefaultAppChecker;

public class ComposeMessageViewController implements DefaultAppChecker.Callback {

    private final ComposeView threadView;
    private final DraftRepository draftRepository;
    private final PhoneNumber phoneNumber;
    private final String composedMessage;
    private final DefaultAppChecker defaultChecker;

    public ComposeMessageViewController(ComposeView threadView, DraftRepository draftRepository, PhoneNumber phoneNumber, String composedMessage, DefaultAppChecker defaultChecker) {
        this.threadView = threadView;
        this.draftRepository = draftRepository;
        this.phoneNumber = phoneNumber;
        this.composedMessage = composedMessage;
        this.defaultChecker = defaultChecker;
    }

    void retrieveDraft() {
        if (TextUtils.isNotEmpty(composedMessage)) {
            threadView.setComposedMessage(composedMessage);
        } else {
            threadView.setComposedMessage(draftRepository.getDraft(phoneNumber));
        }
    }

    void saveDraft() {
        if (TextUtils.isText(threadView.getComposedMessage())) {
            draftRepository.storeDraft(phoneNumber, threadView.getComposedMessage());
        } else {
            draftRepository.clearDraft(phoneNumber);
        }
    }

    @Override
    public void isDefaultSmsApp() {
        threadView.enable();
    }

    @Override
    public void isNotDefaultSmsApp() {
        threadView.disable();
    }

    void start() {
        defaultChecker.checkSmsApp(this);
        retrieveDraft();
    }

    void stop() {
        saveDraft();
    }
}
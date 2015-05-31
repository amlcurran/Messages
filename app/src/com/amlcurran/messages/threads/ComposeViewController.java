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

public class ComposeViewController implements DefaultAppChecker.Callback {

    private final ComposeView composeView;
    private final DraftRepository draftRepository;
    private final PhoneNumber phoneNumber;
    private final String composedMessage;
    private final DefaultAppChecker defaultChecker;
    private boolean preventSend = false;

    public ComposeViewController(ComposeView composeView, DraftRepository draftRepository, PhoneNumber phoneNumber, String composedMessage, DefaultAppChecker defaultChecker) {
        this.composeView = composeView;
        this.draftRepository = draftRepository;
        this.phoneNumber = phoneNumber;
        this.composedMessage = composedMessage;
        this.defaultChecker = defaultChecker;
    }

    void retrieveDraft() {
        if (TextUtils.isNotEmpty(composedMessage)) {
            composeView.setComposedMessage(composedMessage);
        } else {
            composeView.setComposedMessage(draftRepository.getDraft(phoneNumber));
        }
    }

    void saveDraft() {
        if (TextUtils.isText(composeView.getComposedMessage())) {
            draftRepository.storeDraft(phoneNumber, composeView.getComposedMessage());
        } else {
            draftRepository.clearDraft(phoneNumber);
        }
    }

    @Override
    public void isDefaultSmsApp() {
        composeView.enable();
    }

    @Override
    public void isNotDefaultSmsApp() {
        composeView.disable();
    }

    void start() {
        if (!preventSend) {
            defaultChecker.checkSmsApp(this);
        }
        retrieveDraft();
    }

    void stop() {
        saveDraft();
    }

    public void cannotSendToSender() {
        preventSend = true;
        composeView.disable();
    }
}
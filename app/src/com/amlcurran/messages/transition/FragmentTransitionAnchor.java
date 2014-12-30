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

package com.amlcurran.messages.transition;

import android.os.Bundle;

import com.amlcurran.messages.ActivityController;
import com.amlcurran.messages.MmsErrorFragment;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.reporting.StatReporter;
import com.amlcurran.messages.data.ContactFactory;
import com.amlcurran.messages.newcompose.ComposeNewFragment;
import com.amlcurran.messages.ui.control.FragmentController;

class FragmentTransitionAnchor implements TransitionAnchor {

    private final TransitionManager transitionManager;
    private final FragmentController fragmentController;
    private final ActivityController activityController;
    private final StatReporter statReporter;

    public FragmentTransitionAnchor(TransitionManager transitionManager, FragmentController fragmentController, ActivityController activityController, StatReporter statReporter) {
        this.transitionManager = transitionManager;
        this.fragmentController = fragmentController;
        this.activityController = activityController;
        this.statReporter = statReporter;
    }

    @Override
    public TransitionManager newCompose() {
        fragmentController.loadComposeNewFragment();
        return transitionManager;
    }

    @Override
    public TransitionManager thread(Contact contact, String threadId, String writtenMessage) {
        Bundle contactBundle = ContactFactory.smooshContact(contact);
        //ThreadFragment fragment = ThreadFragment.create(threadId, contactBundle, writtenMessage);
        //fragmentController.replaceFragment(fragment);
        activityController.showThreadActivity(threadId, contactBundle, writtenMessage);
        return transitionManager;
    }

    @Override
    public TransitionManager conversationList() {
        fragmentController.loadConversationListFragment();
        return transitionManager;
    }

    @Override
    public TransitionManager newComposeWithMessage(String message) {
        fragmentController.replaceFragment(ComposeNewFragment.withMessage(message));
        return transitionManager;
    }

    @Override
    public TransitionManager newComposeWithNumber(String sendAddress) {
        fragmentController.replaceFragment(ComposeNewFragment.withAddress(sendAddress));
        return transitionManager;
    }

    @Override
    public TransitionManager mmsError() {
        fragmentController.replaceFragment(new MmsErrorFragment());
        statReporter.sendEvent("mms_error_view");
        return transitionManager;
    }
}

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

package com.amlcurran.messages.newcompose;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.HasConversationListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.transition.TransitionManager;

public class ComposeNewController implements ChooserListener {
    private final ComposeNewView composeNewView;
    private final MessagesLoader messagesLoader;
    private final TransitionManager transitionManager;

    public ComposeNewController(ComposeNewView composeNewView, DependencyRepository dependencyRepository) {
        this.composeNewView = composeNewView;
        this.messagesLoader = dependencyRepository.getMessagesLoader();
        this.transitionManager = dependencyRepository.getTransitionManager();
    }

    @Override
    public void recipientChosen(final Contact contact) {
        messagesLoader.getHasConversationWith(contact, new HasConversationListener() {

            @Override
            public void noConversationForNumber() {
                composeNewView.chosenContact(contact);

            }

            @Override
            public void hasConversation(Contact contact, int threadId) {
                transitionManager.to().thread(contact, String.valueOf(threadId), composeNewView.getComposedMessage());
            }
        });
    }
}

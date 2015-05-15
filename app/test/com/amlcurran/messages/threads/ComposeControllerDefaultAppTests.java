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

import android.support.annotation.NonNull;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.data.PhoneNumberOnlyContact;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.core.events.EventSubscriber;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.core.loaders.ThreadListener;
import com.amlcurran.messages.core.threads.MessageTransport;
import com.amlcurran.messages.core.threads.Thread;
import com.amlcurran.messages.telephony.DefaultAppChecker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComposeControllerDefaultAppTests {

    private final TestPhoneNumber testPhoneNumber = new TestPhoneNumber();
    @Mock
    private MessagesLoader loader;
    @Mock
    private DependencyRepository mockRepo;
    @Mock
    private DraftRepository mockDraftRepo;
    private Thread thread;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doAnswer(new ImmediatelyLoadEmptyThread()).when(loader).loadThread(any(String.class), any(ThreadListener.class));
        when(mockRepo.getMessagesLoader()).thenReturn(loader);
        when(mockRepo.getDraftRepository()).thenReturn(mockDraftRepo);
        thread = new Thread(loader, mock(EventSubscriber.class), testPhoneNumber, "14", mock(MessageTransport.class));
    }

    @Test
    public void testNotBeingTheDefaultAppDisablesTheComposeView() {
        AssertingComposeView threadView = new AssertingComposeView();
        DefaultApp appChecker = new DefaultApp();
        ThreadViewController threadViewController = threadViewController(thread, threadView, appChecker);

        threadViewController.start();

        assertThat(threadView.isEnabled, is(false));
    }

    @Test
    public void testBeingTheDefaultAppEnablesTheComposeView() {
        AssertingComposeView threadView = new AssertingComposeView();
        DefaultApp appChecker = new DefaultApp();
        appChecker.isDefault = true;
        ThreadViewController threadViewController = threadViewController(thread, threadView, appChecker);

        threadViewController.start();

        assertThat(threadView.isEnabled, is(true));
    }

    private ThreadViewController threadViewController(Thread thread, ComposeView composeView, DefaultAppChecker appChecker) {
        final PhoneNumberOnlyContact contact = new PhoneNumberOnlyContact(testPhoneNumber);
        return new ThreadViewController(thread, contact, mock(ThreadViewController.ThreadView.class), appChecker, mockRepo, new NeverExecutingScheduledQueue(), new ComposeMessageViewController(composeView, mockDraftRepo, contact.getNumber(), null));
    }

    private static class ImmediatelyLoadEmptyThread implements Answer {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            ThreadListener threadListener = (ThreadListener) invocation.getArguments()[1];
            threadListener.onThreadLoaded(Collections.<SmsMessage>emptyList());
            return null;
        }
    }

    private static class NeverExecutingScheduledQueue implements ScheduledQueue {

        @Override
        public Runnable executeWithDelay(Runnable runnable, long millisDelay) {
            return null;
        }

        @Override
        public void removeEvents(Runnable runnable) {

        }
    }

    private static class DefaultApp extends DefaultAppChecker {

        private boolean isDefault = false;

        public DefaultApp() {
            super(null);
        }

        @Override
        public void checkSmsApp(@NonNull Callback callback) {
            if (isDefault) {
                callback.isDefaultSmsApp();
            } else {
                callback.isNotDefaultSmsApp();
            }
        }
    }

}
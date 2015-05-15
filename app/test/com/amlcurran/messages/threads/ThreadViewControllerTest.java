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

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.data.PhoneNumber;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ThreadViewControllerTest {

    @Mock
    private MessagesLoader loader;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doAnswer(new ImmediatelyLoadEmptyThread()).when(loader).loadThread(any(String.class), any(ThreadListener.class));
    }

    @Test
    public void testLoadingAThreadDoesNotMarkAsReadBeforeTimeout() {
        Thread thread = thread(loader);
        ThreadViewController threadViewController = threadViewController(thread, dependencyRepository(loader), new NeverExecutingScheduledQueue());

        threadViewController.start();

        verify(loader, never()).markThreadAsRead("14");
    }

    private static Thread thread(MessagesLoader loader) {
        return new Thread(loader, mock(EventSubscriber.class), new TestPhoneNumber(), "14", mock(MessageTransport.class));
    }

    private static DependencyRepository dependencyRepository(MessagesLoader loader) {
        DependencyRepository mockRepo = mock(DependencyRepository.class);
        when(mockRepo.getMessagesLoader()).thenReturn(loader);
        when(mockRepo.getDraftRepository()).thenReturn(mock(DraftRepository.class));
        return mockRepo;
    }

    private static ThreadViewController threadViewController(Thread thread, DependencyRepository mockRepo, ScheduledQueue scheduledQueue) {
        return new ThreadViewController(thread, mock(Contact.class), null, new NullThreadView(), mock(DefaultAppChecker.class), mockRepo, scheduledQueue);
    }

    @Test
    public void testLoadingAThreadMarksAsReadAfterTimeout() {
        Thread thread = thread(loader);
        DependencyRepository mockRepo = dependencyRepository(loader);
        ThreadViewController threadViewController = threadViewController(thread, mockRepo, new ImmediatelyExecutingScheduledQueue());

        threadViewController.start();

        verify(loader).markThreadAsRead("14");
    }

    @Test
    public void testStoppingAThreadBeforeTimeoutDoesNotMarkAsRead() {
        Thread thread = thread(loader);
        DependencyRepository mockRepo = dependencyRepository(loader);
        ScheduledQueue scheduledQueue = mock(ScheduledQueue.class);
        ThreadViewController threadViewController = threadViewController(thread, mockRepo, scheduledQueue);

        threadViewController.start();
        threadViewController.stop();

        verify(scheduledQueue).removeEvents(any(Runnable.class));
    }

    private static class ImmediatelyLoadEmptyThread implements Answer {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            ThreadListener threadListener = (ThreadListener) invocation.getArguments()[1];
            threadListener.onThreadLoaded(Collections.<SmsMessage>emptyList());
            return null;
        }
    }

    private static class TestPhoneNumber implements PhoneNumber {
        @Override
        public String flatten() {
            return "1";
        }

        @Override
        public boolean isValid() {
            return true;
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

    private static class NullThreadView implements ThreadViewController.ThreadView {
        @Override
        public void bindContactToHeader(Contact contact) {

        }

        @Override
        public String getComposedMessage() {
            return null;
        }

        @Override
        public void setComposedMessage(String composedMessage) {

        }

        @Override
        public void finish() {

        }

        @Override
        public void scrollTo(int position) {

        }

        @Override
        public void isDefaultSmsApp() {

        }

        @Override
        public void isNotDefaultSmsApp() {

        }
    }

    private class ImmediatelyExecutingScheduledQueue implements ScheduledQueue {
        @Override
        public Runnable executeWithDelay(Runnable runnable, long millisDelay) {
            runnable.run();
            return null;
        }

        @Override
        public void removeEvents(Runnable runnable) {

        }
    }
}
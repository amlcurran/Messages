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
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.data.PhoneNumber;
import com.amlcurran.messages.telephony.DefaultAppChecker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ComposeControllerDraftTests {

    private final TestPhoneNumber testPhoneNumber = new TestPhoneNumber();
    @Mock
    private DependencyRepository mockRepo;
    @Mock
    private DraftRepository mockDraftRepo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockRepo.getDraftRepository()).thenReturn(mockDraftRepo);
    }

    @Test
    public void testLoadingAThreadWithADraftPopulatesTheComposeView() {
        when(mockDraftRepo.getDraft(any(PhoneNumber.class))).thenReturn("hello");
        AssertingComposeView composeView = new AssertingComposeView();
        ComposeViewController controller = new ComposeViewController(composeView, mockDraftRepo, testPhoneNumber, null, mock(DefaultAppChecker.class));

        controller.start();

        assertThat(composeView.composedMessage, is("hello"));
    }

    @Test
    public void testLoadingWithoutAThreadWithADraftDoesntPopulateTheComposeView() {
        when(mockDraftRepo.getDraft(any(PhoneNumber.class))).thenReturn(null);
        AssertingComposeView composeView = new AssertingComposeView();
        ComposeViewController controller = new ComposeViewController(composeView, mockDraftRepo, testPhoneNumber, null, mock(DefaultAppChecker.class));

        controller.start();

        assertNull(composeView.composedMessage);
    }

    @Test
    public void testStoppingTheControllerWithAComposedMessageSavesIt() {
        AssertingComposeView composeView = new AssertingComposeView();
        ComposeViewController controller = new ComposeViewController(composeView, mockDraftRepo, testPhoneNumber, null, mock(DefaultAppChecker.class));

        controller.start();
        composeView.setComposedMessage("hello");
        controller.stop();

        verify(mockDraftRepo).storeDraft(testPhoneNumber, "hello");
    }

    @Test
    public void testStoppingTheControllerWithNoComposedMessageClearsTheDraft() {
        AssertingComposeView composeView = new AssertingComposeView();
        ComposeViewController controller = new ComposeViewController(composeView, mockDraftRepo, testPhoneNumber, null, mock(DefaultAppChecker.class));

        controller.start();
        composeView.setComposedMessage(null);
        controller.stop();

        verify(mockDraftRepo).clearDraft(testPhoneNumber);
    }


}
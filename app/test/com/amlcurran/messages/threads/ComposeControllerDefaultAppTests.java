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
import com.amlcurran.messages.telephony.DefaultAppChecker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ComposeControllerDefaultAppTests {

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
    public void testNotBeingTheDefaultAppDisablesTheComposeView() {
        AssertingComposeView composeView = new AssertingComposeView();
        DefaultApp appChecker = new DefaultApp();
        ComposeViewController composeViewController = new ComposeViewController(composeView, mockDraftRepo, testPhoneNumber, null, appChecker);

        composeViewController.start();

        assertThat(composeView.isEnabled, is(false));
    }

    @Test
    public void testBeingTheDefaultAppEnablesTheComposeView() {
        AssertingComposeView composeView = new AssertingComposeView();
        DefaultApp appChecker = new DefaultApp();
        appChecker.isDefault = true;
        ComposeViewController composeViewController = new ComposeViewController(composeView, mockDraftRepo, testPhoneNumber, null, appChecker);

        composeViewController.start();

        assertThat(composeView.isEnabled, is(true));
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
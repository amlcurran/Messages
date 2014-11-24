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

package com.amlcurran.messages.core.reporting;

import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.core.preferences.PreferenceStore;

import org.junit.Test;

import java.net.URI;
import java.util.Comparator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserPreferenceWrappingStatReporterTest {

    private boolean sendUiEvent_called;

    @Test
    public void whenUsersEnableStatReporting_StatsAreReported() {
        UserPreferenceWrappingStatReporter statReporter = new UserPreferenceWrappingStatReporter(new FakeStatReporter(), new SendStatsPreferenceStore());

        statReporter.sendUiEvent("event");

        assertTrue("Stat wasn't reported", sendUiEvent_called);
    }


    @Test
    public void whenUsersDisableStatReporting_StatsAreNotReported() {
        UserPreferenceWrappingStatReporter statReporter = new UserPreferenceWrappingStatReporter(new FakeStatReporter(), new DontSendStatsPreferenceStore());

        statReporter.sendUiEvent("event");

        assertFalse("Stat was reported", sendUiEvent_called);
    }

    private class FakeStatReporter implements StatReporter {
        @Override
        public void sendUiEvent(String label) {
            sendUiEvent_called = true;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void sendEvent(String label) {

        }
    }

    private class SendStatsPreferenceStore implements PreferenceStore {

        @Override
        public URI getRingtoneUri() {
            return null;
        }

        @Override
        public boolean showNotifications() {
            return false;
        }

        @Override
        public boolean hasNotShownAlphaMessage() {
            return false;
        }

        @Override
        public void storeHasShownAlphaMessage() {

        }

        @Override
        public boolean isNotificationPersistent() {
            return false;
        }

        @Override
        public boolean showLargeUnreadPreviews() {
            return false;
        }

        @Override
        public boolean shouldSendStats() {
            return true;
        }

        @Override
        public Sort getConversationSort() {
            return null;
        }

        @Override
        public Comparator<Conversation> getConversationSortComparator() {
            return null;
        }
    }

    private class DontSendStatsPreferenceStore implements PreferenceStore {

        @Override
        public URI getRingtoneUri() {
            return null;
        }

        @Override
        public boolean showNotifications() {
            return false;
        }

        @Override
        public boolean hasNotShownAlphaMessage() {
            return false;
        }

        @Override
        public void storeHasShownAlphaMessage() {

        }

        @Override
        public boolean isNotificationPersistent() {
            return false;
        }

        @Override
        public boolean showLargeUnreadPreviews() {
            return false;
        }

        @Override
        public boolean shouldSendStats() {
            return false;
        }

        @Override
        public Sort getConversationSort() {
            return null;
        }

        @Override
        public Comparator<Conversation> getConversationSortComparator() {
            return null;
        }
    }

}
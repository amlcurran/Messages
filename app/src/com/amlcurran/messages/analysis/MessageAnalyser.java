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

package com.amlcurran.messages.analysis;

import android.webkit.URLUtil;

import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.data.Time;

import java.util.List;

public class MessageAnalyser {
    private String body;

    public MessageAnalyser(String body) {
        this.body = body;
    }

    public static Time getLatestMessageTime(List<Conversation> conversations) {
        Time latest = Time.fromMillis(0);
        for (Conversation conversation : conversations) {
            if (conversation.getTimeOfLastMessage().isLaterThan(latest)) {
                latest = conversation.getTimeOfLastMessage();
            }
        }
        return latest;
    }

    public boolean hasLink() {
        return getLink() != null;
    }

    public Link getLink() {
        String[] chunks = body.split("\\s+");
        for (String chunk : chunks) {
            if (URLUtil.isValidUrl(chunk)) {
                return new Link(chunk);
            }
        }
        return null;
    }
}

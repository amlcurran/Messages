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

package com.amlcurran.messages.core.conversationlist;

import java.util.Comparator;

public class UnreadComparator implements Comparator<Conversation> {

    private final TimestampComparator timestampComparator = new TimestampComparator();

    @Override
    public int compare(Conversation o1, Conversation o2) {
        if (o1.isRead() && !o2.isRead()) {
            return +1;
        } else if (!o1.isRead() && o2.isRead()) {
            return -1;
        }
        return timestampComparator.compare(o1, o2);
    }

}

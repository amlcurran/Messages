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

public class TimestampComparator implements Comparator<Conversation> {

    @Override
    public int compare(Conversation o1, Conversation o2) {
        return o1.getTimeOfLastMessage().isLaterThan(o2.getTimeOfLastMessage()) ? -1 : +1;
    }

}

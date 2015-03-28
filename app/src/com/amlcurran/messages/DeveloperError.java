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

package com.amlcurran.messages;

public class DeveloperError extends Error {

    public DeveloperError(String detailMessage) {
        super(detailMessage);
    }

    public static void throwIfNull(Object object) {
        throwIf(object == null, "Developer error - object was null");
    }

    public static void throwIf(boolean predicate) {
        throwIf(predicate, "Developer error");
    }

    public static void throwIf(boolean predicate, String detailMessage) {
        if (predicate) {
            throw new DeveloperError(detailMessage);
        }
    }
}

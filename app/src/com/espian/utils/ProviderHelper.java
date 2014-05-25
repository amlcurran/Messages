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

package com.espian.utils;

public class ProviderHelper<T> {

    private Class<T> requiredClass;

    public ProviderHelper(Class<T> requiredClass) {
        this.requiredClass = requiredClass;
    }

    public T get(Object implementer) {
        try {
            return requiredClass.cast(implementer);
        } catch (ClassCastException cce) {
            String detailMessage = String.format("%1$s doesn't implement the required interface %2$s",
                    implementer.getClass().getSimpleName(), requiredClass.getSimpleName());
            throw new ClassCastException(detailMessage);
        }
    }

}

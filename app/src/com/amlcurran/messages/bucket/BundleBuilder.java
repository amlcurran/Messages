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

package com.amlcurran.messages.bucket;

import android.os.Bundle;

public class BundleBuilder {

    private final Bundle bundle;

    public BundleBuilder() {
        bundle = new Bundle();
    }

    public BundleBuilder put(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public Bundle build() {
        return bundle;
    }

    public BundleBuilder put(String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleBuilder put(String key, long value) {
        bundle.putLong(key, value);
        return this;
    }

    public BundleBuilder put(String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleBuilder put(String key, Bundle value) {
        bundle.putParcelable(key, value);
        return this;
    }
}

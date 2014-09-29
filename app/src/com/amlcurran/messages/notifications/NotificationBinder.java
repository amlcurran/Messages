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

package com.amlcurran.messages.notifications;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;

public class NotificationBinder {

    private final NotificationCompat.Builder baseBuilder;

    public NotificationBinder(NotificationCompat.Builder builder) {
        this.baseBuilder = builder;
    }

    public void setExtender(NotificationCompat.Extender extender) {
        failIfNoNotification();
        baseBuilder.extend(extender);
    }

    public void setStyle(NotificationCompat.Style style) {
        failIfNoNotification();
        baseBuilder.setStyle(style);
    }

    public Notification build() {
        return baseBuilder.build();
    }

    public NotificationCompat.Builder getBaseBuilder() {
        return baseBuilder;
    }

    private void failIfNoNotification() {
        if (baseBuilder == null) {
            throw new NullPointerException("Set a Builder first");
        }
    }

}

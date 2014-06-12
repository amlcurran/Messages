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

package com.amlcurran.messages.ui;

import android.app.Activity;

import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.MessagesLoader;

public interface ViewController {
    boolean backPressed();

    void hideDisabledBanner();

    void showDisabledBanner();

    void hideSecondary();

    void showSecondary();

    void setContentView(Activity activity);

    void setUpActionBar();

    void showSelectedContact(Contact contact, MessagesLoader messagesLoader);

    public interface Callback {
        void secondaryVisible();

        void secondaryHidden();

        void defaultsBannerPressed();

        void secondarySliding(float slideOffset);

        void newMessageButtonClicked();
    }
}

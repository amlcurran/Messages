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

import com.amlcurran.messages.telephony.DefaultAppChecker;
import com.amlcurran.messages.ui.NewMessageButtonController;

class HideNewComposeAndShowBannerCallback implements DefaultAppChecker.Callback {
    private final NewMessageButtonController newComposeController;
    private final DisabledBannerController disabledBannerController;

    public HideNewComposeAndShowBannerCallback(NewMessageButtonController newComposeController, DisabledBannerController disabledBannerController) {
        this.newComposeController = newComposeController;
        this.disabledBannerController = disabledBannerController;
    }

    @Override
    public void isDefaultSmsApp() {
        disabledBannerController.hideBanner();
        newComposeController.enableNewMessageButton();
    }

    @Override
    public void isNotDefaultSmsApp() {
        disabledBannerController.showBanner();
        newComposeController.disableNewMessageButton();
    }
}

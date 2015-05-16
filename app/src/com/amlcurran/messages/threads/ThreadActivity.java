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

package com.amlcurran.messages.threads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.amlcurran.messages.ActivityController;
import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.ExternalEventManager;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.core.data.DraftRepository;
import com.amlcurran.messages.core.loaders.MessagesLoader;
import com.amlcurran.messages.core.preferences.PreferenceStore;
import com.amlcurran.messages.preferences.PreferenceStoreDraftRepository;
import com.amlcurran.messages.preferences.SharedPreferenceStore;
import com.amlcurran.messages.reporting.LoggingStatReporter;
import com.amlcurran.messages.transition.TransitionManager;
import com.amlcurran.messages.ui.actionbar.ActionBarHeaderCallback;
import com.amlcurran.messages.ui.actionbar.HoloActionBarController;
import com.amlcurran.messages.ui.control.FragmentController;
import com.amlcurran.messages.ui.control.TwoPaneFullScreenFragmentViewController;

public class ThreadActivity extends ActionBarActivity implements DependencyRepository, FragmentController.FragmentCallback {

    private TransitionManager transitionManager;
    private ExternalEventManager externalEventManager;
    private PreferenceStore preferencesStore;
    private DraftRepository draftRepository;
    private MessagesLoader messagesLoader;
    private HoloActionBarController actionBarController;
    private ActionBarHeaderCallback headerCreationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);
        actionBarController = new HoloActionBarController(getSupportActionBar());
        messagesLoader = SingletonManager.getMessagesLoader(this);
        preferencesStore = new SharedPreferenceStore(this);
        draftRepository = new PreferenceStoreDraftRepository(this);

        headerCreationCallback = new ActionBarHeaderCallback(actionBarController);
        FragmentController fragmentController = new TwoPaneFullScreenFragmentViewController(this, this, headerCreationCallback);
        ActivityController activityController = new ActivityController(this, null);
        transitionManager = new TransitionManager(fragmentController, activityController, new LoggingStatReporter());
        externalEventManager = new ExternalEventManager(activityController, getContentResolver(), new LoggingStatReporter());

        insertThreadFragment(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        insertThreadFragment(intent);
    }

    private void insertThreadFragment(Intent intent) {
        ThreadFragment fragment = ThreadFragment.create(
                intent.getStringExtra(ThreadFragment.THREAD_ID),
                intent.getBundleExtra(ThreadFragment.CONTACT),
                intent.getStringExtra(ThreadFragment.COMPOSED_MESSAGE));

        getFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .commit();

        headerCreationCallback.addCustomHeader(fragment.getHeaderView(this));
        secondaryVisible();
    }

    public static Intent intent(Context context, String threadId, Bundle contactBundle, String writtenMessage) {
        Intent intent = new Intent(context, ThreadActivity.class);
        intent.putExtra(ThreadFragment.THREAD_ID, threadId)
                .putExtra(ThreadFragment.CONTACT, contactBundle)
                .putExtra(ThreadFragment.COMPOSED_MESSAGE, writtenMessage);
        return intent;
    }


    @Override
    public TransitionManager getTransitionManager() {
        return transitionManager;
    }

    @Override
    public ExternalEventManager getExternalEventManager() {
        return externalEventManager;
    }

    @Override
    public PreferenceStore getPreferenceStore() {
        return preferencesStore;
    }

    @Override
    public DraftRepository getDraftRepository() {
        return draftRepository;
    }

    @Override
    public MessagesLoader getMessagesLoader() {
        return messagesLoader;
    }

    @Override
    public void insertedDetail() {
    }

    @Override
    public void insertedMaster() {

    }

    @Override
    public void secondaryVisible() {
        actionBarController.showHeader();
    }

    @Override
    public void secondaryHidden() {

    }

    @Override
    public void secondarySliding(float slideOffset) {

    }

}

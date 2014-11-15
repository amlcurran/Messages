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

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.amlcurran.messages.bucket.BundleBuilder;

public class Dialog extends DialogFragment {

    private static final String TITLE = "title";
    private static final String TEXT = "message";
    private static final String NEGATIVE_LABEL = "button_neg";
    private static final String POSITIVE_LABEL = "button_pos";
    private BlockingInUiNotifier.Callbacks callbacks;

    public static Dialog create(String title, String message, Button negative, Button positive) {
        Dialog dialog = new Dialog();

        Bundle bundle = new BundleBuilder()
                .put(TITLE, title)
                .put(TEXT, message)
                .build();

        if (negative != null) {
            bundle.putString(NEGATIVE_LABEL, negative.label);
        }
        if (positive != null) {
            bundle.putString(POSITIVE_LABEL, positive.label);
        }

        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString(TITLE))
                .setMessage(getArguments().getString(TEXT));

        String negativeLabel = getArguments().getString(NEGATIVE_LABEL);
        if (negativeLabel != null) {
            builder.setNegativeButton(negativeLabel, standardCallbacks);
        }

        String positiveLabel = getArguments().getString(POSITIVE_LABEL);
        if (positiveLabel != null) {
            builder.setPositiveButton(positiveLabel, standardCallbacks);
        }

        return builder.create();
    }

    public Dialog setCallbacks(BlockingInUiNotifier.Callbacks callbacks) {
        this.callbacks = callbacks;
        return this;
    }

    public static class Button {
        private final String label;

        public Button(String label) {
            this.label = label;
        }
    }

    private DialogInterface.OnClickListener standardCallbacks = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                callbacks.positive();
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                callbacks.negative();
            }
        }
    };

}

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

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.amlcurran.messages.R;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog, container, false);

        ((TextView) view.findViewById(R.id.dialog_title)).setText(getArguments().getString(TITLE));
        ((TextView) view.findViewById(R.id.dialog_text)).setText(getArguments().getString(TEXT));

        TextView negativeButton = (TextView) view.findViewById(R.id.dialog_button_negative);
        String negativeLabel = getArguments().getString(NEGATIVE_LABEL);
        setUpButton(negativeButton, negativeLabel);

        TextView positiveButton = (TextView) view.findViewById(R.id.dialog_button_positive);
        String positiveLabel = getArguments().getString(POSITIVE_LABEL);
        setUpButton(positiveButton, positiveLabel);

        return view;
    }

    private void setUpButton(TextView button, String label) {
        button.setText(label);
        button.setOnClickListener(buttonListener);
        button.setVisibility(TextUtils.isEmpty(label) ? View.GONE : View.VISIBLE);
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        setStyle(Dialog.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog);
        return dialog;
    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.dialog_button_positive) {
                callbacks.positive();
                dismiss();
            } else if (v.getId() == R.id.dialog_button_negative) {
                callbacks.negative();
                dismiss();
            }
        }
    };

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
}

package com.amlcurran.messages.threads.binder;

import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.analysis.SmsMessageAnalyser;
import com.amlcurran.messages.core.data.SmsMessage;
import com.amlcurran.messages.threads.ResendCallback;

public class ViewHolder extends RecyclerView.ViewHolder {

    private final TextView bodyText;
    private final ImageView icon;
    private final TextView secondaryText;
    private final View sendingImage;
    private final ResendCallback resendCallback;
    private SmsMessageAnalyser smsMessageAnalyser;

    public ViewHolder(View view, SmsMessageAnalyser smsMessageAnalyser, ResendCallback resendCallback) {
        super(view);
        this.resendCallback = resendCallback;
        this.bodyText = ((TextView) view.findViewById(android.R.id.text1));
        this.icon = ((ImageView) view.findViewById(R.id.failed_to_send_image));
        this.sendingImage = view.findViewById(R.id.sending_image);
        this.secondaryText = ((TextView) view.findViewById(android.R.id.text2));
        this.smsMessageAnalyser = smsMessageAnalyser;
        if (this.icon != null){
            //VOM
            this.icon.setColorFilter(view.getResources().getColor(R.color.theme_alt_color_2));
        }
    }

    void hideSendingIcon() {
        animateOutView(sendingImage);
    }

    void showSendingIcon() {
        if (sendingImage != null) {
            sendingImage.setVisibility(View.VISIBLE);
            sendingImage.setAlpha(1f);
        }
    }

    void hideFailedIcon() {
        animateOutView(icon);
    }

    private void animateOutView(final View icon) {
        if (icon != null) {
            icon.animate()
                    .alpha(0f)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            icon.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

    void showFailedIcon(SmsMessage smsMessage) {
        icon.setVisibility(View.VISIBLE);
        icon.setAlpha(1f);
        icon.setOnClickListener(new ViewModelFactory.ResendClickListener(smsMessage, resendCallback));
    }

    void showFailedText() {
        secondaryText.setText(R.string.failed_to_send_touch_to_resend);
    }

    void setBodyText(SmsMessage smsMessage) {
        bodyText.setText(smsMessage.getBody());
        Linkify.addLinks(bodyText, Linkify.ALL);
    }

    void addTimestampView(SmsMessage smsMessage) {
        secondaryText.setText(smsMessageAnalyser.getDifferenceToNow(smsMessage.getTimestamp()));
    }

    void showSendingText() {
        secondaryText.setText(R.string.sendingdotdotdot);
    }
}

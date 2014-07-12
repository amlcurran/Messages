package com.amlcurran.messages.core.events;

import com.amlcurran.messages.core.TextUtils;

public class Broadcast {

    private final String action;
    private final String filter;

    public Broadcast(String action, String filter) {
        this.action = action;
        this.filter = filter;
    }

    public String getAction() {
        return action;
    }

    public boolean hasFilter() {
        return !TextUtils.isEmpty(filter);
    }

    public String getFilter() {
        return filter;
    }
}

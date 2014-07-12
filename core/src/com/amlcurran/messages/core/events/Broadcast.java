package com.amlcurran.messages.core.events;

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
}

package com.github.event;


public class ClientStopEvent {
    private final int message;

    public ClientStopEvent(int message) {
        this.message = message;
    }

    public int getMessage() {
        return message;
    }
}

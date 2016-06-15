package com.grayraven.eclogintest.PoJos;
public class MessageEvent {
    public final int message;
    public final static int LOG_OUT_MSG = 1;

    public MessageEvent(int message) {
        this.message = message;
    }
}

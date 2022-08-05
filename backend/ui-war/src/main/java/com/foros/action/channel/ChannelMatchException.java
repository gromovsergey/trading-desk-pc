package com.foros.action.channel;

public class ChannelMatchException extends Exception{
    private String message;

    public ChannelMatchException(String message, Throwable e) {
        super(e);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.foros.monitoring;

public class MonitorActivationException extends Exception {
    public MonitorActivationException(String message) {
        super(message);
    }

    public MonitorActivationException(String message, Exception e) {
        super(message + " Cause: " + e);
    }
}

package com.foros.session.security;

/**
 * Author: Boris Vanin
 */
public class UserNotFoundException extends Exception {

    private Object object;

    public UserNotFoundException(String subject, Object object) {
        super(subject + " not found! Details: " + object.toString());
        this.object = object;
    }

    public UserNotFoundException(String subject, Object object, Throwable cause) {
        super(subject + " not found! Details: " + object.toString(), cause);
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

}

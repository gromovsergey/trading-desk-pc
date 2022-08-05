package com.foros.action.xml;

/**
 * Author: Boris Vanin
 * Date: 27.11.2008
 * Time: 12:36:57
 * Version: 1.0
 */
public class ProcessException extends Exception {

    public ProcessException(String message) {
        super(message);
    }

    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}

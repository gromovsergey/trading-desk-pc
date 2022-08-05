package com.foros.util;

/**
 * Author: Boris Vanin
 * Date: 29.10.2008
 * Time: 15:29:58
 * Version: 1.0
 */
public class MessageHelper {

    public static String prepareMessageKey(String name) {
        return name.replaceAll("[\\s\\\\/,]+", "-");
    }

}

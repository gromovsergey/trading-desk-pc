package com.foros.util;

/**
 * Author: Boris Vanin
 */
public class HashUtil {

    public static int calculateHash(Object... objects) {
        int result = 0;

        for (Object object : objects) {
            result = 31 * result + (object != null ? object.hashCode() : 0);
        }

        return result;
    }

}

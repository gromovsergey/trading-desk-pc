package com.foros.cache.generic.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

public abstract class Hash {

    public static String toBase64(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    public static String toHex(byte[] bytes) {
        return new BigInteger(1, bytes).toString(16);
    }

    public static MessageDigest getMd5MessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

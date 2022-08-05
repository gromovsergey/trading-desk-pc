package com.foros.rs.client.util;

import com.foros.rs.client.RsConstants;
import com.foros.rs.client.RsException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class MacUtils {

    private static Mac initMac() {
        try {
            return Mac.getInstance(RsConstants.ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RsException(e);
        }
    }

    public static String encode(String text, SecretKey key) {
        Mac mac = initMac();
        try {
            mac.init(key);
        } catch (InvalidKeyException e) {
            throw new RsException(e);
        }

        byte[] bytes;
        try {
            bytes = mac.doFinal(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RsException("Can't believe that");
        }

        return DatatypeConverter.printBase64Binary(bytes);
    }

}

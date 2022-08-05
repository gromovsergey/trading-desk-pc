package com.foros.birt.web.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Hasher {

    private static final char[] HEX_CHARS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String hash(Map<String, Object> map) {
        try {
            return hashImpl(map);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String hashImpl(Map<String, Object> map) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        ArrayList<String> keys = new ArrayList<String>(map.keySet());

        Collections.sort(keys);

        for (String key : keys) {
            md.update(key.getBytes("UTF-8"));
            Object value = map.get(key);
            if (value != null) {
                md.update(value.toString().getBytes("UTF-8"));
            }
        }

        return toHexString(md.digest());
    }

    private static String toHexString(byte[] bytes) {
        char chars[] = new char[32];

        for (int i = 0; i < chars.length; i = i + 2) {
            byte b = bytes[i / 2];
            chars[i] = HEX_CHARS[(b >>> 0x4) & 0xf];
            chars[i + 1] = HEX_CHARS[b & 0xf];
        }

        return new String(chars);
    }

}

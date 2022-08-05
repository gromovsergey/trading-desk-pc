package com.foros.cache.generic.hasher;

import com.foros.cache.generic.serializer.Serializer;
import com.foros.cache.generic.util.Hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

public class Md5Hasher implements Hasher {

    private Serializer serializer;

    public Md5Hasher(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public String hash(Object object) {
        MessageDigest messageDigest = Hash.getMd5MessageDigest();
        char[] chars = object.getClass().getName().toCharArray();
        ByteBuffer bytes = ByteBuffer.allocate(chars.length * 2);
        bytes.order(ByteOrder.LITTLE_ENDIAN);
        for (char ch : chars) {
            bytes.putChar(ch);
        }
        bytes.rewind();
        messageDigest.update(bytes);
        messageDigest.update(serializer.serialize(object));
        return Hash.toBase64(messageDigest.digest());
    }
}

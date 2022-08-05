package com.foros.security.generator;

import java.security.SecureRandom;
import org.springframework.security.crypto.codec.Hex;

public class SimpleKeyGenerator implements KeyGenerator {

    private SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate(int size) {
        byte[] key = new byte[size];

        secureRandom.nextBytes(key);

        return String.valueOf(Hex.encode(key));
    }

}

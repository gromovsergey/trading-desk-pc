package com.foros.util;

import java.math.BigDecimal;
import java.util.Random;

public class RandomUtil {
    private static final long DEFAULT_MAX_LONG = 1000L;

    public enum Alphabet {
        LETTERS("ABSDEFGHIJKLMNOPQRSTUVWXQZabcdefghijklmnopqrstuvw"),
        NUMBERS ("1234567890"),
        WHITESPACE(" \t\n");

        private final String value;

        Alphabet(String values) {
            this.value = values;
        }

        public String getValue() {
            return value;
        }
    }

    private static Random RANDOM = new Random();

    /**
     * Return a random string from specified alphabet string with a fixed lenght.
     *
     * @param alphabet - source string for values to be generated
     * @param length - length of result string
     * @return a randomly generated string
     */
    private static String getRandomString(String alphabet, int length) {
        if (alphabet == null) {
            throw  new IllegalArgumentException("Alphabet string can't be null");
        }

        if (length < 1) {
            throw  new IllegalArgumentException("Length must be at least one element long");
        }

        int alphabetLength = alphabet.length();
        char[] randomString = new char[length];

        Random random = new Random();

        for (int i = 0; i < randomString.length; i++) {
            randomString[i] = alphabet.charAt(random.nextInt(alphabetLength));
        }
        return new String(randomString);
    }

    public static String getRandomString() {
        return getRandomString(10, Alphabet.LETTERS);
    }

    public static String getRandomString(int length) {
        return getRandomString(length, Alphabet.LETTERS);
    }

    public static String getRandomString(int length, Alphabet ... alphabets) {
        StringBuilder resultAlphabet = new StringBuilder();

        for (Alphabet alphabet : alphabets) {
            resultAlphabet.append(alphabet.value);
        }
        return getRandomString(resultAlphabet.toString(), length);
    }

    public static Long getRandomLong() {
        return getRandomLong(DEFAULT_MAX_LONG);
    }

    /**
     * @param maxLong - max positive value of queried random long.
     * @return a random long value from 0 inclusive till maxLong exclusive
     */
    public static Long getRandomLong(long maxLong) {
        if (maxLong <= 0) {
            throw new IllegalArgumentException("MaxLong must be positive");
        }
        return Math.abs(RANDOM.nextLong() % maxLong);
    }

    public static int getRandomInt(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("End value must be more then start");
        }

        return start + RANDOM.nextInt(end - start);
    }

    public static int getRandomInt(int n) {
        return getRandomInt(0, n);
    }

    public static BigDecimal getRandomBigDecimal() {
        return new BigDecimal(RANDOM.nextDouble());
    }
}

package com.foros.reporting.serializer.formatter;

import org.apache.commons.codec.digest.DigestUtils;

public class HashedValueFormatter extends ValueFormatterSupport<Object> {
    public enum ALGORITHM {
        MD5, SHA
    }

    private ALGORITHM algorithm;
    private String salt;

    public HashedValueFormatter(ALGORITHM algorithm, String salt) {
        if (algorithm == null) {
            throw new IllegalArgumentException("Algorithm can not be null");
        }
        this.algorithm = algorithm;
        this.salt = salt;
    }

    @Override
    public String formatText(Object value, FormatterContext context) {
        switch (algorithm) {
        case MD5:
            return DigestUtils.md5Hex(getValue(value));
        default:
            return DigestUtils.shaHex(getValue(value));
        }
    }

    private String getValue(Object value) {
        return (salt == null ? "" : salt) + String.valueOf(value);
    }
}
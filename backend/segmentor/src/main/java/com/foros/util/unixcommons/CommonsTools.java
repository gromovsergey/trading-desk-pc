package com.foros.util.unixcommons;

public interface CommonsTools {

    String normalizeURL(String originalUrl) throws Exception;

    String normalizeKeyword(String originalKeyword) throws Exception;

    boolean validateURL(String originalUrl) throws Exception;
}

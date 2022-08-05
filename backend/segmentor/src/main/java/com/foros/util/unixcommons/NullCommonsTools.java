package com.foros.util.unixcommons;

public class NullCommonsTools implements CommonsTools {

    @Override
    public String normalizeURL(String originalUrl) throws Exception {
        return originalUrl;
    }

    @Override
    public String normalizeKeyword(String originalKeyword) throws Exception {
        return originalKeyword.replace("-", "");
    }

    @Override
    public boolean validateURL(String originalUrl) throws Exception {
        return true;
    }
}

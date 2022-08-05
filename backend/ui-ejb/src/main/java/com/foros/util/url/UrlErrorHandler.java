package com.foros.util.url;

public interface UrlErrorHandler {
    void invalidURL();

    void invalidPort(String value);

    void httpPortOnly();

    void invalidUserinfo(String value);

    void emptyHost();

    void invalidHost(String value);

    void invalidSchema(String[] schemas);
}

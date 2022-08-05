package com.foros.util.url;

public class EmptyUrlErrorHandler implements UrlErrorHandler {
    public static final  UrlErrorHandler INSTANCE = new EmptyUrlErrorHandler();

    @Override
    public void invalidURL() {
    }

    @Override
    public void invalidPort(String value) {
    }

    @Override
    public void httpPortOnly() {
    }

    @Override
    public void invalidUserinfo(String value) {
    }

    @Override
    public void emptyHost() {
    }

    @Override
    public void invalidHost(String value) {
    }

    @Override
    public void invalidSchema(String[] schemas) {
    }
}

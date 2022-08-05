package com.foros.util.unixcommons;

public class UnixCommonsTools implements CommonsTools {
    private static final String INIT_SUCCESS = "INIT_SUCCESS";

    static {
        System.loadLibrary(System.getProperty("foros.httpfunctions.lib"));
        String result = initialize();
        if (!INIT_SUCCESS.equals(result)) {
            throw new UnsatisfiedLinkError("Can't load or initialize libraries for UnixCommonsTools reason:" + result + " !");
        } else {
            System.out.println("UnixCommonsTools has been loaded");
        }
    }

    private static native String initialize();

    public native String normalizeURL(String originalUrl) throws Exception;

    public native String normalizeKeyword(String originalKeyword) throws Exception;

    public native boolean validateURL(String originalUrl) throws Exception;
}

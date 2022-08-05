package com.foros.util.unixcommons;

public class ExternalValidation {

    public static boolean validateUrl(String url) {
        try {
            return CommonToolsInstance.get().validateURL(url);
        } catch (Exception e) {
            return false;
        }
    }
}

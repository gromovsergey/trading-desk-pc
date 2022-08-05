package com.foros.util.url;

public class TriggerURLValidator {

    public static boolean isValid(String url) {
        return isValid(new EmptyUrlErrorHandler(), url);
    }

    public static boolean isValid(UrlErrorHandler handler, String url) {
        if (url == null) {
            return false;
        }

        if (url.startsWith("\"") && url.endsWith("\"")) {
            url = url.length() > 1 ? url.substring(1, url.length() - 1) : "";
        }

        if (!URLValidator.isValidURLTrigger(handler, url)) {
            return false;
        }

        if (!URLValidator.isValidURLTrigger(new EmptyUrlErrorHandler(),
                TriggerQANormalization.normalizeURL(url))) {
            handler.invalidURL();
            return false;
        }

        return true;
    }
}

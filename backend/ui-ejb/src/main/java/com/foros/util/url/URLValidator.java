package com.foros.util.url;

import java.net.IDN;

import org.apache.commons.lang.ArrayUtils;

import com.foros.util.Schema;

public class URLValidator {

    private static final String[] DEFAULT_SCHEMES = {"http", "https"};

    private static final String[] DEFAULT_SCHEMES_WITH_RELATIVE = {"http://", "https://", "//"};

    private static final String[] TRIGGER_SCHEMAS = new String[] {"http", null};

    public static String urlForValidate(String value, String[] schemas) {
        if (value != null && schemas != null && schemas.length > 0) {
            for (String schema : schemas) {
                if (value.toLowerCase().startsWith(schema)) {
                    return Schema.HTTP.getValue() + value.substring(schema.length());
                }
            }
        }
        return value;
    }

    public static String urlForValidate(String value) {
        return urlForValidate(value, DEFAULT_SCHEMES_WITH_RELATIVE);
    }

    public static boolean isValid(String url) {
        return isValid(EmptyUrlErrorHandler.INSTANCE, url, DEFAULT_SCHEMES);
    }

    public static boolean isValid(String url, String[] schemas) {
        return isValid(EmptyUrlErrorHandler.INSTANCE, url, schemas);
    }

    public static boolean isValid(UrlErrorHandler handler, String url) {
        return isValid(handler, url, DEFAULT_SCHEMES);
    }


    public static boolean isValid(UrlErrorHandler handler, String url, String[] schemas) {
        if (url == null) {
            return true;
        }

        RfcURL rfcURL = new RfcURL(url);

        if (!validateCommon(rfcURL, handler, schemas)) {
            return false;
        }

        if (!validatePort(handler, rfcURL, false)) {
            return false;
        }

        return true;
    }

    public static boolean isValidURLTrigger(UrlErrorHandler handler, String url) {
        if (url == null) {
            return true;
        }

        RfcURL rfcURL = new RfcURL(url);

        if (!validateCommon(rfcURL, handler, TRIGGER_SCHEMAS)) {
            return false;
        }

        if (!validatePort(handler, rfcURL, true)) {
            return false;
        }

        if (rfcURL.getHost().startsWith("-") || TriggerQANormalization.normalizeURL(url).startsWith("-")) {
            handler.invalidHost(rfcURL.getHost());
            return false;
        }

        return true;
    }

    private static boolean validateCommon(RfcURL rfcURL, UrlErrorHandler handler, String[] schemas) {
        if (!rfcURL.isValid()) {
            handler.invalidURL();
            return false;
        }

        return validateUserinfo(handler, rfcURL) &&
               validateHost(handler, rfcURL) &&
               validateSchema(handler, rfcURL, schemas);
    }

    private static boolean validateUserinfo(UrlErrorHandler handler, RfcURL rfcURL) {
        String userinfo = rfcURL.getUserinfo();
        if (userinfo != null && !RfcURL.USERINFO_PATTERN.matcher(userinfo).matches()) {
            handler.invalidUserinfo(userinfo);
            return false;
        }

        return true;
    }

    private static boolean validateHost(UrlErrorHandler handler, RfcURL rfcURL) {
        String host = rfcURL.getHost();
        if (host == null || host.length() < 1) {
            handler.emptyHost();
            return false;
        }

        String punycodedHost;
        try {
            punycodedHost = IDN.toASCII(host);
        } catch (Exception e) {
            handler.invalidHost(host);
            return false;
        }

        if (!RfcURL.HOST_PATTERN.matcher(punycodedHost).matches()) {
            handler.invalidHost(host);
            return false;
        }

        return true;
    }

    private static boolean validatePort(UrlErrorHandler handler, RfcURL rfcURL, boolean httpPortOnly) {
        String portStr = rfcURL.getPort();
        if (portStr != null && !"".equals(portStr)) {
            try {
                Integer port = Integer.valueOf(portStr);

                if (httpPortOnly) {
                    if (port != null && !port.equals(80)) {
                        handler.httpPortOnly();
                        return false;
                    }
                }
            } catch (NumberFormatException e) {
                handler.invalidPort(portStr);
                return false;
            }
        }

        return true;
    }

    private static boolean validateSchema(UrlErrorHandler handler, RfcURL rfcURL, String[] schemas) {
        if (!ArrayUtils.contains(schemas, rfcURL.getScheme())) {
            handler.invalidSchema(schemas);
            return false;
        }

        return true;
    }

}

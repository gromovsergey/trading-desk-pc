package com.foros.util.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RfcURL {
    /**
     * BNF for URI (RFC3986) uses the following expression:
     * "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?"
     *
     * But we use the expression that differs slightly, "//" is a part of scheme group instead of host group.
     * Example:
     * As per RFC3986 '//google.com' is correct url and 'google.com' is incorrect one (in case schemaless URLs are allowed), because host should begin with '//'.
     * But we think '//google.com' is incorrect and 'google.com' is correct, because '//' is a part of scheme.
     */
    public static final String URL_REGEX =
            "^(([^:/?#]+)://)?(([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";
    //        12              34          5       6  7         8 9
    public static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public static final String AUTHORITY_REGEX = "^((.+)@)?([^:]*)(:(.+))?$";
    public static final Pattern AUTHORITY_PATTERN = Pattern.compile(AUTHORITY_REGEX);

    public static final String USERINFO_REGEX = "^(?>%[\\dA-F]{2}|[\\w-.~!$&'()*+,;=:])*$";
    public static final Pattern USERINFO_PATTERN = Pattern.compile(USERINFO_REGEX);

    public static final String HOST_REGEX = "^(?>%[\\dA-F]{2}|[\\w-.~!$&'()*+,;=])*$";
    public static final Pattern HOST_PATTERN = Pattern.compile(HOST_REGEX);

    private String scheme;
    private String authority;
    private String userinfo;
    private String host;
    private String port;
    private String path;
    private String query;
    private boolean valid = false;

    public RfcURL(String value) {
        if (value == null) {
            return;
        }

        Matcher urlMatcher = URL_PATTERN.matcher(value);

        if (!urlMatcher.matches()) {
            return;
        }

        scheme = urlMatcher.group(2);
        if (scheme != null) {
            scheme = scheme.toLowerCase();
        }

        authority = notEmpty(urlMatcher.group(4));
        path = notEmpty(urlMatcher.group(5));
        query = notEmpty(urlMatcher.group(7));

        if (authority != null) {
            Matcher authorityMatcher = AUTHORITY_PATTERN.matcher(authority);
            if (!authorityMatcher.matches()) {
                return;
            }

            userinfo = notEmpty(authorityMatcher.group(2));
            host = notEmpty(authorityMatcher.group(3));
            port = notEmpty(authorityMatcher.group(5));
        }

        valid = true;
    }

    private String notEmpty(String str) {
        return str != null && str.isEmpty() ? null : str;
    }

    public String getScheme() {
        return scheme;
    }

    public String getAuthority() {
        return authority;
    }

    public String getUserinfo() {
        return userinfo;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public boolean isValid() {
        return valid;
    }
}

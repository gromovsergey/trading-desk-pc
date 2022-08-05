package com.foros.util.url;

public class TriggerNormalization {
    // Utility class
    private TriggerNormalization() { }

    /** Normalize per rules initially described in OUI-25036 */
    public static String normalizeURL(String url) {
        RfcURL rfcURL = new RfcURL(url);
        StringBuilder res = new StringBuilder(url.length());

        if (rfcURL.getUserinfo() != null) {
            res.append(rfcURL.getUserinfo());
            res.append("@");
        }

        // remove protocol (http://google.com -> google.com)
        // remove www (www.google.com - google.com)
        String host = rfcURL.getHost();
        if (host != null) {
            host = host.replaceFirst("^([wW]{3}\\.)*([^/]+)\\.(.+)", "$2.$3");
            res.append(host);
        }

        // remove / from end of trigger if path is empty (google.com/ -> google.com)
        if (rfcURL.getPath() != null && !"/".equals(rfcURL.getPath())) {
            res.append(rfcURL.getPath());
        }

        // remove port (google.com:80 -> google.com)
        // remove anchor (google.com#anchor -> google.com)

        if (rfcURL.getQuery() != null) {
            res.append('?');
            res.append(rfcURL.getQuery());
        }

        return res.toString();
    }
}

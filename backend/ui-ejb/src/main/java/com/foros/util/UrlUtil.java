package com.foros.util;

import static com.foros.config.ConfigParameters.CREATIVES_PATH;
import static com.foros.config.ConfigParameters.DATA_URL;

import com.foros.config.Config;
import com.foros.session.fileman.FileUtils;
import com.foros.util.url.RfcURL;

import java.util.regex.Pattern;


public class UrlUtil {
    private static final Pattern HTTP_SCHEMA_PATTERN = Pattern.compile("^" + Schema.HTTP.getValue(), Pattern.CASE_INSENSITIVE);
    private static final Pattern HTTPS_SCHEMA_PATTERN = Pattern.compile("^" + Schema.HTTPS.getValue(), Pattern.CASE_INSENSITIVE);

    /**
     * Removes tailing slash "/" if exists.
     *
     * @param url the url to process.
     * @return url string without tailing slash "/"
     */
    public static String stripUrl(String url) {
        if (url != null && url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * Formats a file url so it can be viewed by opening corresponding url
     * @return a well formatted string which represent a file's url
     */
    public static String formatFileUrl(String fullFileName, Config config) {
        String dataUrl = config.get(DATA_URL);
        if (dataUrl == null) {
            dataUrl = "";
        }
        String creativesPath = config.get(CREATIVES_PATH);
        if (creativesPath == null) {
            creativesPath = "";
        }

        StringBuffer url = new StringBuffer(stripUrl(dataUrl));
        url.append("/").append(creativesPath).append("/").append(FileUtils.extractPathName(StringUtil.trimProperty(fullFileName, "null")));
        return url.toString();
    }

    /**
     * Checks if the passing parammeter is a URL that starts with "http://" or "https://".
     * If URL is null or empty then false is returned.
     *
     * @param url the value to check.
     * @return true if value starts with "http://" or "https://", false otherwise.
     */
    public static boolean isSchemaUrl(String url) {
        return isSchemaUrl(url, false);
    }

    /**
     * Checks if the passing parammeter is a URL that starts with "http://" or "https://".
     * If URL is null or emptry false is returned.
     *
     * @param url the value to check.
     * @param allowRelative is relative allowed
     * 
     * @return true if value starts with "http://" or "https://" and "//" if allowed, false otherwise.
     */
    public static boolean isSchemaUrl(String url, boolean allowRelative) {
        if (StringUtil.isPropertyEmpty(url)) {
            return false;
        }
        String passbackLowCase = url.toLowerCase();
        if (allowRelative) {
            return StringUtil.startsWith(passbackLowCase, "http://", "https://", "//");
        } else {
            return StringUtil.startsWith(passbackLowCase, "http://", "https://");
        }
    }

    public static String stripSchema(String url) {
        if (StringUtil.isPropertyEmpty(url)) {
            return url;
        }

        String stripped = StringUtil.replaceRegexp(url, HTTP_SCHEMA_PATTERN, "");
        if (stripped.equals(url)) {
            stripped = StringUtil.replaceRegexp(url, HTTPS_SCHEMA_PATTERN, "");
        }
        return stripped;
    }

    public static String truncateUrlToDomainName(String url) {
        RfcURL rfcUrl = new RfcURL(url);
        return rfcUrl.isValid() ? rfcUrl.getHost() : url;
    }

    /**
     * Append schema part to the URL if URL doesn't have one.
     * @param url url
     * @return url whith schema
     */
    public static String appendSchema(Schema schema, String url) {
        if (StringUtil.isPropertyEmpty(url)) {
            return url;
        }

        if (isSchemaUrl(url)) {
            return url;
        }

        return schema.getValue() + url;
    }

    public static String concat(String... parts) {
        if (parts == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(100);
        for (String part : parts) {
            if(part == null || "".equals(part)) {
                // skip empty parts
                continue;
            }

            boolean first = sb.length() == 0;

            // make current url end with /
            if (!first && sb.charAt(sb.length() - 1) != '/') {
                sb.append('/');
            }

            // append the next part
            if (!first && part.charAt(0) == '/') {
                sb.append(part, 1, part.length());
            } else {
                sb.append(part);
            }

        }
        return sb.toString();
    }

    public static String replaceUidParamValue(String url, String value) {
        String param = "uid";
        if (url.indexOf("?") == -1) {
            return url + "?" + param + "=" + value;
        }

        if (url.indexOf(param) == -1) {
            return url + "&amp;" + param + "=" + value;
        }


        int uidIndex = url.indexOf(param + "=");
        String newUrl = url.substring(0, uidIndex + param.length() + 1);
        if (url.indexOf("&amp;", uidIndex) == -1) {
            newUrl += value;
        } else {
            newUrl += value + url.substring(url.indexOf("&amp;", uidIndex));
        }

        return newUrl;
    }
}

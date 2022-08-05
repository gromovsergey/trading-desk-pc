package com.foros.util.url;

import com.foros.session.channel.triggerQA.TriggerQAType;
import com.foros.util.unixcommons.TriggerNormalization;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import org.apache.commons.lang.ArrayUtils;

public class TriggerQANormalization {
    private static final int INFINITE_LOOP_THRESHOLD = 2000;

    public static String normalizeTrigger(String countryCode, TriggerQAType type, String trigger) {
        switch (type) {
            case KEYWORD:
                return normalizeKeyword(countryCode, trigger);
            case URL:
                return normalizeURL(trigger);
            default:
                throw new IllegalArgumentException("TriggerType must be defined!");
        }
    }

    public static String normalizeKeyword(String countryCode, String keyword) {
        return TriggerNormalization.normalizeKeyword(countryCode, keyword);
    }

    /**
     * Normalize url trigger according Trigger QA rules.
     * @param url any VALID trigger url
     * @return normalized url
     */
    public static String normalizeURL(String url) {
        PreparedUrl preparedUrl = new PreparedUrl(url);
        String result = preparedUrl.getUnwrapped();
        result = decodePercents(result, INFINITE_LOOP_THRESHOLD);
        result = encodeSpaces(result);
        result = lowerAsciiChars(result);
        result = processParts(result);
        return preparedUrl.wrap(result);
    }

    private static String decodePercents(String url, int left) {
        if (left < 0) {
            throw new RuntimeException("Infinite loop: " + url);
        }

        int length = url.length();
        StringBuilder sb = new StringBuilder(length > 500 ? length / 2 : length);

        for (int pos = 0; pos < length;) {
            char ch = url.charAt(pos);
            if (ch == '%') {
                pos = new FragmentDecoder(url, sb).decode(pos);
            } else {
                pos++;
                sb.append(ch);
            }
        }

        String decodedUrl = sb.toString();
        if (decodedUrl.equals(url)) {
            return url;
        } else {
            return decodePercents(decodedUrl, left - 1);
        }

    }

    private static String encodeSpaces(String url) {
        StringBuilder res = new StringBuilder(url.length() + 4); // for two spaces
        for (int i = 0; i < url.length(); i++) {
            char ch = url.charAt(i);
            if (ch != ' ') {
                res.append(ch);
            } else {
                res.append("%20");

            }
        }
        return res.toString();
    }

    private static String lowerAsciiChars(String url) {
        char[] chars = url.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch >= 'A' && ch <= 'Z') {
                chars[i] = Character.toLowerCase(ch);
            }
        }
        return new String(chars);
    }

    private static String processParts(String url) {
        RfcURL rfcURL = new RfcURL(url);
        StringBuilder res = new StringBuilder(url.length());

        if (rfcURL.getUserinfo() != null) {
            res.append(rfcURL.getUserinfo());
            res.append("@");
        }

        String host = rfcURL.getHost();
        if (host != null) {
            host = host.replaceFirst("^([wW]{3}\\.)*([^/]+)\\.(.+)", "$2.$3");
            res.append(host);
        }

        if (rfcURL.getPath() == null) {
            res.append('/');
        } else {
            res.append(rfcURL.getPath());
        }

        if (rfcURL.getQuery() != null) {
            res.append('?');
            res.append(rfcURL.getQuery());
        }

        return res.toString();

    }

    private static class PreparedUrl {

        private boolean isQuoted;
        private String unwrapped;

        public PreparedUrl(String url) {
            url = url.trim();
            if (url.startsWith("\"") && url.endsWith("\"")) {
                isQuoted = true;
                url = url.length() > 1 ? url.substring(1, url.length() - 1) : "";
            }

            unwrapped = url.trim();
        }

        public boolean isQuoted() {
            return isQuoted;
        }

        public String getUnwrapped() {
            return unwrapped;
        }

        public String wrap(String url) {
            if (!isQuoted) {
                return url;
            }

            StringBuilder res = new StringBuilder(url.length() + 3);

            if(isQuoted) {
                res.append('"');
            }

            res.append(url);

            if(isQuoted) {
                res.append('"');
            }
            return res.toString();
        }
    }

    private static class FragmentDecoder {
        private static final Charset UTF = Charset.forName("UTF-8");
        private static final char[] IGNORING_CHARS = (" " + ":/?#[]/@" + "!$&'()*+,;=" + "\0").toCharArray();

        private ByteBuffer bytes;
        private String url;
        private StringBuilder sb;

        private FragmentDecoder(String url, StringBuilder sb) {
            this.url = url;
            this.sb = sb;
        }

        private int decode(int pos) {

            while(pos < url.length() && url.charAt(pos) == '%') {
                // too short
                if (pos + 2 >= url.length()) {
                    flush();
                    sb.append(url.substring(pos));
                    return url.length();
                }

                char ch1 = url.charAt(pos + 1);
                char ch2 = url.charAt(pos + 2);

                // is not "%" HEXDIG HEXDIG
                if (!isHex(ch1) || !isHex(ch2)) {
                    flush();
                    sb.append(url.charAt(pos));
                    return pos + 1;
                }

                if (bytes == null) {
                    // (url.length() - pos) / 3 is an upper bound for the number of remaining bytes
                    bytes = ByteBuffer.allocate((url.length() - pos) / 3);
                }

                bytes.put((byte) ((Character.digit(ch1, 16) << 4) + Character.digit(ch2, 16)));
                pos += 3;
            }

            flush();
            return pos;
        }

        private void flush() {
            int sbLength = sb.length();
            if (bytes == null) {
                return;
            }

            bytes.limit(bytes.position());
            bytes.position(0);

            try {
                CharBuffer decoded = UTF.newDecoder().decode(bytes);
                while(decoded.hasRemaining()) {
                    char decodedChar = decoded.get();
                    if (ArrayUtils.contains(IGNORING_CHARS, decodedChar)) {
                        sb.append("%");
                        sb.append(String.format("%02x", (int) decodedChar));
                    } else {
                        sb.append(decodedChar);
                    }
                }
            } catch (CharacterCodingException e) {
                // remove everything we add
                sb.setLength(sbLength);
                bytes.position(0);
                while (bytes.hasRemaining()) {
                    sb.append("%");
                    int b = bytes.get() & 0xFF;
                    sb.append(Integer.toHexString(b));
                }
            }
            bytes.clear();
        }

        private static boolean isHex(char ch) {
            return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
        }
    }
}

package com.foros.util.preview;

import com.foros.session.creative.PreviewException;
import com.foros.util.Function;
import com.foros.util.StringUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;

public class SubstitutionTemplate {
    private final List<Object> content;
    private final int estimatedSize;

    public SubstitutionTemplate(List<Object> content, int estimatedSize) {
        this.content = content;
        this.estimatedSize = estimatedSize;
    }

    public List<Substitution> getSubstitutions() {
        List<Substitution> result = new ArrayList<>();
        for (Object o : content) {
            if (o instanceof Substitution) {
                result.add((Substitution) o);
            }
        }
        return result;
    }

    public String render(Function<String, String> replacementResolver) {
        StringBuilder buf = new StringBuilder(estimatedSize);
        for (Object chunk : content) {
            if (chunk instanceof Substitution) {
                Substitution substitution = (Substitution) chunk;
                String val = replacementResolver.apply(substitution.getToken());
                if (val == null || val.isEmpty()) {
                    val = substitution.getDefaultValue();
                }

                if (val == null) {
                    val = "";
                }
                buf.append(getEncodedValue(val, substitution.getPrefix()));
            } else {
                buf.append(chunk);
            }

        }
        return buf.toString();
    }

    public static String getEncodedValue(String value, String tokenPrefix) {
        if (StringUtil.isPropertyEmpty(tokenPrefix)) {
            return value;
        }
        TokenPrefix tokenPref = TokenPrefix.byName(tokenPrefix);
        switch (tokenPref) {
            case UTF8:
                return value;
            case MIME_URL:
                return StringUtil.encodeUrl(value);
            case XML:
                return StringEscapeUtils.escapeXml(value);
            case JS:
                return convertToHexAscii(value);
            case JS_UNICODE:
                return convertToHexUnicode(value);
            default:
                throw new IllegalArgumentException();
        }
    }

    private static String convertToHexUnicode(String str) {
        StringWriter out = new StringWriter();
        escapeJavaStyleUnicode(out, str);
        return out.toString();
    }

    private static String convertToHexAscii(String str) {
        StringWriter out = new StringWriter();
        escapeJavaStyleString(out, str);
        return out.toString();
    }

    private static void escapeJavaStyleUnicode(Writer out, String str) {
        try {
            if (out == null) {
                throw new IllegalArgumentException("The Writer must not be null");
            }
            if (str == null) {
                return;
            }
            int sz = str.length();
            for (int i = 0; i < sz; i++) {
                char ch = str.charAt(i);
                if (StringUtil.isBetween(ch, '0', '9') || // Number?
                        StringUtil.isBetween(ch, 'A', 'Z') || // Upper Letter
                        StringUtil.isBetween(ch, 'a', 'z')) { // Lowercase letter
                    out.write(ch);
                } else {
                    hexUnicode(out, ch);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static void hexUnicode(Writer out, char ch) throws IOException {
        String leadingZeros = "0000" + hex(ch);
        out.write("\\u" + leadingZeros.substring(leadingZeros.length() - 4));
    }

    private static void escapeJavaStyleString(Writer out, String str) {
        try {
            if (out == null) {
                throw new IllegalArgumentException("The Writer must not be null");
            }
            if (str == null) {
                return;
            }
            int sz = str.length();
            for (int i = 0; i < sz; i++) {
                char ch = str.charAt(i);
                if (ch == '\n' || ch == '\r' || ch == '\'' || ch == '"' || ch == '\\' || ch == '/' || ch == '<') {
                    out.write("\\x" + hex(ch));
                } else {
                    out.write(ch);
                }
            }
        } catch (IOException e) {
            //
        }
    }

    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }

    public enum TokenPrefix {
        UTF8("utf8"),
        MIME_URL("mime-url"),
        XML("xml"),
        JS("js"),
        JS_UNICODE("js-unicode");

        private static final Set<String> NAMES = Collections.unmodifiableSet(new HashSet<String>() {
            {
                for (TokenPrefix prefix : values()) {
                    add(prefix.getName());
                }
            }
        });

        private final String name;

        TokenPrefix(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static TokenPrefix byName(String name) {
            TokenPrefix tokenPrefix = byNameOptional(name);
            if (tokenPrefix == null) {
                throw new PreviewException("Illegal prefix name given: '" + name + "'");
            }
            return tokenPrefix;
        }

        public static TokenPrefix byNameOptional(String name) {
            for (TokenPrefix prefix : values()) {
                if (prefix.getName().equals(name)) {
                    return prefix;
                }
            }
            return null;
        }

        public static Set<String> names() {
            return NAMES;
        }
    }
}

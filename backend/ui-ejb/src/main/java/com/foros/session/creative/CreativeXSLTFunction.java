package com.foros.session.creative;

/**
 * The XSLT-external functions implementation.
 * @author Vitaliy_Knyazev
 */
public class CreativeXSLTFunction {
    /**
     * Replace all special characters with \\xXX sequence, where XX is the hex code of the value.
     * @param str a source string
     * @param needEscape do encode ?
     * @return encoded string
     */
    public static String escapeJs(String str, Boolean needEscape) {
        if (!needEscape.booleanValue() || str == null) {
            return str;
        }
        
        int last = 0;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < str.length(); i ++) {
            switch (str.charAt(i)) {
                case '\\':
                case '"':
                case '\'':
                case '/':
                case '\n':
                case '\r': {
                    if (last > 0) {
                        buf.append(str.substring(i - last, i));
                        last = 0;
                    }
                    buf.append(String.format("\\x%02X", (int)str.charAt(i)));
                    break;
                }
                
                default: {
                    last ++;
                    break;
                }    
            }
        }
        if (last > 0) {
            buf.append(str.substring(str.length() - last, str.length()));
        }

        return buf.toString();
    }

    public static String escapeJs(String str) {
        return escapeJs(str, Boolean.TRUE);
    }
    
    /**
     * Replace all non-alphanumeric characters (i.e. not 0-9, A-Z and a-z) with \\xXX sequence,
     * where XX is the hex number of UCS-2 (2-byte Universal Character Set) value.
     * @param str a source string
     * @param needEscape do encode ?
     * @return encoded string
     */
    public static String escapeJsUnicode(String str, Boolean needEscape) {
        if (!needEscape.booleanValue() || str == null) {
            return str;
        }
        
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < str.length(); i ++) {
            char ch = str.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
                buf.append(ch);
            } else {
                // the Java strings are always UCS-2 encoded
                buf.append(String.format("\\x%02X", (int)ch));
            }
        }
        
        return buf.toString();
    }
    
    public static String escapeJsUnicode(String str) {
        return escapeJsUnicode(str, Boolean.TRUE);
    }
}

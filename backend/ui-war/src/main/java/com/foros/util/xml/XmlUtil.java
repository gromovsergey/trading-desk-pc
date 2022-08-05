package com.foros.util.xml;

import com.foros.util.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;


/**
 * Author: Boris Vanin
 * Date: 21.11.2008
 * Time: 14:39:19
 * Version: 1.0
 */
public class XmlUtil {
    public static final String TAG_NAME_VALIDATION_REGEXP = "^[\\p{L}\\p{Nd}\\.\\-& ]+$";

    public static boolean validateTagName(String tagName) {
        return StringUtil.isPropertyNotEmpty(tagName) && tagName.length() <= 50 && tagName.matches(TAG_NAME_VALIDATION_REGEXP);
    }

    /**
     * Xml text generator
     */
    public static final class Generator {

        private Generator() {
        }

        public static String tag(String tagName, String id, String value) {
            if (value != null) {
                return tagWithText(tagName, id, value);
            } else {
                return emptryTag(tagName);
            }
        }

        public static String tag(String tagName, String value) {
            return tag(tagName, null, value);
        }

        public static String tag(String tagName, Number value) {
            return tag(tagName, value != null ? String.valueOf(value) : null);
        }

        public static String tag(String tagName, Boolean value) {
            return tag(tagName, value != null ? Boolean.toString(value) : null);
        }

        public static String tag(String tagName, Object value) {
            return tag(tagName, value != null ? value.toString() : null);
        }

        public static String tagWithText(String tagName, String id, String value) {
            return "<" + tagName + createAttribute("id", id) + ">" + StringEscapeUtils.escapeXml(value) + "</" + tagName + ">\n";
        }

        private static String createAttribute(String name, String value) {
            if (value != null) {
                return " " + name + "='" + StringEscapeUtils.escapeXml(value) + "'";
            } else {
                return "";
            }
        }

        public static String emptryTag(String tagName) {
            return "<" + tagName + "/>\n";
        }

    }

}

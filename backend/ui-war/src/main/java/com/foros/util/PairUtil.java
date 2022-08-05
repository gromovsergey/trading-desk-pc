package com.foros.util;

import org.apache.commons.lang.StringUtils;


public class PairUtil {
    private static final String PAIR_TEMPLATE = "%s_%s";

    /**
     * factory method to create pairs sepatated by '_' char
     *
     * @param value
     * @param name
     * @return
     */
    public static String createAsString(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("Pair value cound not be null");
        }
        return String.format(PAIR_TEMPLATE, value.toString(), StringUtils.defaultIfEmpty(name, ""));
    }

    /**
     * parse string number_string
     * where number is used for filter and string is used for description in page
     *
     * @param idValueStr string witch contains a pair sepatated by '_' char
     * @return pair
     */
    public static NameValuePair<String, String> parseIdNamePair(String idValueStr) {
        NameValuePair<String, String> pair = new NameValuePair<String, String>();

        if (idValueStr != null) {
            int pos = idValueStr.indexOf("_");
            if (pos == -1) {
                pair.setValue(idValueStr);
            } else {
                pair.setValue(idValueStr.substring(0, pos));
                pair.setName(idValueStr.substring(pos + 1));
            }
        }

        return pair;
    }

    public static boolean validatePair(String pair) {
        if (StringUtil.isPropertyEmpty(pair)) {
            return false;
        }
        int index = pair.indexOf("_");
        return index > 0 && index < pair.length();
    }

    public static Long fetchId(String pair) {
        if (validatePair(pair)) {
            NameValuePair<String, String> result = parseIdNamePair(pair);
            if (result.getValue().equals("null")) {
                return null;
            } else {
                return new Long(result.getValue());
            }
        } else {
            return Long.valueOf(pair);
        }
    }

}

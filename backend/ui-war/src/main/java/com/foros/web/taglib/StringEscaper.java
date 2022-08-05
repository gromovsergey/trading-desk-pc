package com.foros.web.taglib;

import com.foros.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidParameterException;

import org.apache.commons.lang.StringEscapeUtils;

public class StringEscaper {
    public StringEscaper() {
    }

    /**
     * This method acts as escapteJavaScript method, but also escapes html characters: <p/>
     * For example, say we have a link : &lt;a onclick="handleOnclick ('He didn't say: "Stop  &lt;attack&gt;" ')" /&gt;
     * after applying this method to script agrument the result will be: <p/>
     * &lt;a onclick="handleOnclick ('He didn\'t say: &amp;quot;Stop  &amp;lt;attack&amp;gt;&amp;quot; ')" /&gt;
     */
    public static String escapeJavaScriptInTag(String input) {
        return StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeJavaScript(input));
    }

    public static String formatText(String text) {
        if (StringUtil.isPropertyEmpty(text)) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();

        String[] lines = StringUtil.splitAndTrim(text);
        for (String line : lines) {
            result.append(StringEscapeUtils.escapeHtml(line) + "<br>");
        }

        return result.toString();
    }

    public static String shortString(String str, int maxSymbols) {
        str = str.replaceAll("\\s+"," ");
        if (maxSymbols < 4) {
            throw new InvalidParameterException("Unable to make string shorten than 4 symbol");
        }
        
        if (str.length() < maxSymbols) {
            return str;
        }
        
        return str.substring(0, maxSymbols - 3) + "...";
    }

    public static String escapeId(String name) {
        // escape any possible values that can make the ID painful to work with in JavaScript
        if (name != null) {
            return name.replaceAll("[\\.\\[\\]]", "_");
        } else

        return "";
    }

    /**
     * Escape url or part of url using URLEncoder with UTF-8 encoding 
     * @param url url to escape
     * @return escaped url
     */
    public static String escapeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // It's quite impossible
            throw new RuntimeException(e);
        }
    }

    /**
     * Escapes the value of a given Resource key from properties file
     *
     * @param key Resource Key whose value need tobe escaped
     * @return escaped String
     */
    public static String formatMessage(String key) {
        return StringEscapeUtils.escapeJavaScript(StringUtil.getLocalizedString(key));
    }

    public static String escapeJavaScript(String str) {
        return org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(str);
    }

    public static String extractUrlFromTrigger(String string) {
        return StringUtil.extractUrlFromTrigger(string, true);
    }

    public static String escapePropertyName(String name) {
        // replace all blanks with underscores
        if (name != null) {
            return name.replaceAll(" ", "_");
        }

        return name;
    }
}


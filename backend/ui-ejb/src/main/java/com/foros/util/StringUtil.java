package com.foros.util;

import com.foros.cache.local.LocalCacheValuesProducer;
import com.foros.cache.local.LocalizedResourcesLocalCache;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.ServiceLocator;
import com.foros.util.customization.CustomizationHelper;
import com.foros.util.i18n.LocalizationUtil;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityNotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

public class StringUtil {
    private static final Pattern IS_UNQUOTE = Pattern.compile("^\"(.*)\"$");
    public  static final Pattern ONLY_COLOR_LETTERS = Pattern.compile("^[1234567890ABCDEFabcdef]{6}+$");
    public static final Pattern BANNED_FOR_XML = Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]");
    public static final Pattern ALLOWED_FOR_XML = Pattern.compile("[\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]");
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String END_LINE_SYMBOL = "\r\n";
    private static final String CARRIAGE_TAB_SYMBOL = "\r\t";
    private static final String NEW_LINE_SYMBOL = "\n";
    private static final String COMMA_AND_NEW_LINE_CHARS = ",\n";
    private static final String WHITE_SPACES = "\t\r\n \u00A0\u2007\u202F";
    private static final Comparator<String> LEXICAL_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return StringUtil.compareToIgnoreCase(s1, s2);
        }
    };

    private StringUtil() {
    }

    public static int compareToIgnoreCase(String s1, String s2) {
        if (s1 == s2) {
            return 0;
        }
        if (s1 == null) {
            return -1;
        }
        if (s2 == null) {
            return 1;
        }
        return s1.toLowerCase().compareTo(s2.toLowerCase());
    }

    public static int lexicalCompare(String s1, String s2) {
        int res = StringUtil.compareToIgnoreCase(s1, s2);
        return res != 0 ? res : s1.compareTo(s2);
    }

    public static Comparator<String> getLexicalComparator() {
        return LEXICAL_COMPARATOR;
    }

    public static boolean isPropertyEmpty(String property) {
        return ((property == null) || (property.trim().equals("")));
    }

    public static boolean isPropertyNotEmpty(String property) {
        return !isPropertyEmpty(property);
    }

    public static String formatBigDecimal(BigDecimal digit, int roundTo, boolean padding) {
        if (digit == null) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance(getLocale());
        if (padding && digit.setScale(0, RoundingMode.DOWN).toString().equals(digit.setScale(0, RoundingMode.UP).
                toString())) {
            return nf.format(digit.setScale(0, RoundingMode.HALF_UP));
        } else {
            return nf.format(digit.setScale(roundTo, RoundingMode.HALF_UP));
        }
    }

    private static Locale getLocale() {
        return CurrentUserSettingsHolder.getLocale();
    }

    public static boolean isPropertyEmpty(Character property) {
        return ((property == null) || (Character.isWhitespace(property.charValue())) || (property == '\u0000'));
    }

    public static boolean isPropertyNotEmpty(Character property) {
        return !isPropertyEmpty(property);
    }

    public static String trimProperty(String property) {
        return trimProperty(property, null);
    }

     public static String trimProperty(String property, String nullString) {
        return (property != null ? StringUtils.strip(property, WHITE_SPACES) : nullString);
    }

    public static Long convertToLong(String value) throws NumberFormatException {
        return StringUtil.isPropertyEmpty(value) ? null : Long.parseLong(value);
    }

    public static Integer convertToInt(String value) throws NumberFormatException {
        return StringUtil.isPropertyEmpty(value) ? null : Integer.parseInt(value);
    }

    public static String encodeUrl(String url) {
        try {
            url = URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    public static boolean isNumber(String value) {
        try {
            Long.valueOf(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    /**
     * If file is a zip-archive returns name of file, without zip extension.
     * @param fileName full name of file
     * @return name of file without .zip extension
     * @since 06-Feb-2008
     */
    public static String removeZipSuffix(String fileName) {
        if (fileName != null && fileName.endsWith(".zip")) {
            return fileName.substring(0, fileName.length() - 4);
        }

        return fileName;
    }

    /**
     * Extracts file name from full path.
     *
     * @param fileName full path to the file.
     * @return name of file.
     */
    public static String trimFileName(String fileName) {
        if (fileName == null) {
            return null;
        }

        fileName = fileName.trim();
        int index1 = fileName.lastIndexOf("%5C");
        if (index1 != -1) index1 += 3;
        int index2 = fileName.lastIndexOf("%5c");
        if (index2 != -1) index2 += 3;
        int index3 = fileName.lastIndexOf("%2F");
        if (index3 != -1) index3 += 3;
        int index4 = fileName.lastIndexOf("%2f");
        if (index4 != -1) index4 += 3;
        int index5 = fileName.lastIndexOf('/');
        if (index5 != -1) index5 ++;
        int index6 = fileName.lastIndexOf('\\');
        if (index6 != -1) index6 ++;

        int index = Math.max(Math.max(Math.max(Math.max(Math.max(index1, index2), index3), index4), index5), index6);

        if (index == -1) {
            return fileName;
        }

        return fileName.substring(index);
    }

    public static int countCharsCJK(String str) {
        int count = 0;
        int length = str.length();

        for (int i = 0; i < length;) {
            int codePoint = str.codePointAt(i);
            if (isCodePointCJK(codePoint)) {
                count += 2;
            } else {
                count++;
            }
            i += Character.charCount(codePoint);
        }

        return count;
    }

    private static boolean isCodePointCJK(int codePoint) {
        if (codePoint < 0x1100) {
            return false;
        }

        return isBetween(codePoint, 0x4E00, 0x9FFF)
                || isBetween(codePoint, 0xFA0C, 0xFAFF)
                || isBetween(codePoint, 0x3400, 0x4DBF)
                || isBetween(codePoint, 0x20000, 0x2A6DF)
                || isBetween(codePoint, 0x2F800, 0x2FA1F)
                || isBetween(codePoint, 0x3040, 0x30FF)
                || isBetween(codePoint, 0xFF65, 0xFF9F)
                || isBetween(codePoint, 0x31F0, 0x31FF)
                || isBetween(codePoint, 0xAC00, 0xD7A3)
                || isBetween(codePoint, 0x3130, 0x318F)
                || isBetween(codePoint, 0xF900, 0xFA0B)
                || isBetween(codePoint, 0x1100, 0x11FF)
                || isBetween(codePoint, 0xFFA0, 0xFFDC);
    }

    public static boolean isBetween(int x, int min, int max) {
        return x >= min && x <= max;
    }

    /**
     * Simple method to execute toString methods if object may be null.
     * @param object if null returns empty string, object.toString() otherwise
     * @return return "" if object it null, or object.toString() otherwise.
     */
    public static String toString(Object object) {
        if (object == null) {
            return "";
        }

        return object.toString();
    }

    public static Long toLong(String strId) {
        return toLong(strId, false);
    }

    public static Long toLong(String strId, boolean optional) {
        if (optional && isPropertyEmpty(strId)) {
            return null;
        }

        try {
            return Long.valueOf(strId);
        } catch (NumberFormatException e) {
            throw new EntityNotFoundException("Entity with id = " + (strId == null ? "null" : "'" + strId + "'") + " not found");
        }
    }

    public static Integer toInt(String strId) {
        try {
            return Integer.parseInt(strId);
        } catch (NumberFormatException e) {
            //TODO: This function uses not only for parsing entity id and this exception has useless message
            throw new EntityNotFoundException("Entity with id = " + (strId == null ? "null" : strId) + " not found");
        }
    }

    public static String getLocalizedString(String key) {
        return getLocalizedString(key, false);
    }

    public static String getLocalizedString(String key, boolean emptyDefaultValue) {
        Locale locale = CurrentUserSettingsHolder.getLocale();

        return getLocalizedString(key, locale, emptyDefaultValue);
    }

    public static String getLocalizedString(String key, Locale locale) {
        return getLocalizedString(key, locale, false);
    }

    public static String getLocalizedString(String key, Locale locale, boolean emptyDefaultValue) {
        return getLocalizedString(key, locale, emptyDefaultValue, (Object[])null);
    }

    public static String getLocalizedString(String key, Object... args) {
        return getLocalizedString(key, CurrentUserSettingsHolder.getLocale(), args);
    }

    public static String getLocalizedStringWithDefault(String key, String defaultValue, Object... args) {
        return getLocalizedStringWithDefault(key, defaultValue, CurrentUserSettingsHolder.getLocale(), args);
    }

    public static String getLocalizedString(String key, Locale locale, Object... args) {
        return getLocalizedString(key, locale, false, args);
    }

    static String getLocalizedString(String key, Locale locale, boolean emptyDefaultValue, Object... args) {
        return getLocalizedStringWithDefault(key, locale, null, emptyDefaultValue, args);
    }

    public static String getLocalizedStringWithDefault(String key, String defaultValue, Locale locale, Object... args) {
        return getLocalizedStringWithDefault(key, locale, defaultValue, defaultValue == null, args);
    }

    private static String getLocalizedStringWithDefault(String key, Locale locale, String defaultValue, boolean emptyDefaultValue, Object... args) {
        String result = getCachedLocalizedString(key, locale);
        if (result == null) {
            if (defaultValue != null) {
                result = defaultValue;
            } else {
                result = emptyDefaultValue ? null : "???" + locale + key + "???";
            }
        } else {
            result = formatMessage(result, locale, args);
        }
        return result;
    }

    private static String getCachedLocalizedString(final String key, final Locale locale) {
        LocalizedResourcesLocalCache resourcesLocalCache = ServiceLocator.getInstance().lookup(LocalizedResourcesLocalCache.class);
        String cacheKey = getLocalizedStringCachePrefix() + locale + key;
        String result = (String) resourcesLocalCache.get(cacheKey, new LocalCacheValuesProducer() {
            @Override
            public Object getValue() {
                ResourceBundle resourceBundle = getBundle(locale);
                return resourceBundle.getString(key);
            }
        });
        return result;
    }

    public static String formatMessage(String pattern, Locale locale, Object... args) {
        if (args == null || args.length == 0) {
            return pattern;
        }

        MessageFormat format = new MessageFormat(pattern, locale);

        Format[] formats = format.getFormats();
        if (formats.length == 0) {
            return pattern;
        }

        return format.format(args);
    }

    public static String getLocalizedBigDecimal(BigDecimal value) {
        return getLocalizedBigDecimal(value, -1);
    }

    static String getLocalizedBigDecimal(BigDecimal value, int roundTo) {
        if (value == null) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance(getLocale());
        return getLocalizedBigDecimal(value, roundTo, nf);
    }

    private static String getLocalizedBigDecimal(BigDecimal value, int roundTo, NumberFormat nf) {
        if (value != null) {
            if (roundTo >= 0) {
                nf.setMaximumFractionDigits(roundTo);
                nf.setMinimumFractionDigits(roundTo);
            } else {
                int scale = value.scale() < 0 ? 0 : value.scale();
                nf.setMaximumFractionDigits(scale);
                nf.setMinimumFractionDigits(scale);
            }
            return nf.format(value);
        } else {
            return null;
        }
    }

    public static ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle("resource.applicationResource", locale);
    }

    public static ResourceBundle getBundle() {
        return getBundle(LocalizationUtil.getCurrentLocale());
    }

    public static boolean startsWith(String text, String ... starts) {
        if (text != null) {
            for (String start : starts) {
                if (text.startsWith(start)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean endsWith(String text, String ... ends) {
        if (text != null) {
            for (String end : ends) {
                if (text.endsWith(end)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean equalsWith(String text, String ... equals) {
        if (text != null) {
            for (String equal : equals) {
                if (text.equals(equal)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean equalsWithIgnoreCase(String text, String ... equals) {
        if (text != null) {
            for (String equal : equals) {
                if (text.equalsIgnoreCase(equal)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Split string by , or \n separators.
     * All empty token are preserved
     * @param text test to split
     * @return array of tokens
     */
    public static String[] splitByComma(String text) {
        if (StringUtil.isPropertyEmpty(text)) {
            return new String[0];
        }
        return StringUtils.splitPreserveAllTokens(text.trim(), COMMA_AND_NEW_LINE_CHARS);
    }

    /**
     * Split string by \n or \r\n separators.
     * All empty token are preserved
     * @param text test to split
     * @return array of tokens
     */
    public static String[] splitByLines(String text) {
        if (StringUtil.isPropertyEmpty(text)) {
            return new String[0];
        }
        return StringUtils.splitPreserveAllTokens(StringUtils.replace(text, END_LINE_SYMBOL, NEW_LINE_SYMBOL), NEW_LINE_SYMBOL);
    }

    public static int getBytesCount(String text) {
        try {
            return (text != null) ? text.trim().getBytes(DEFAULT_ENCODING).length : -1;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Can't find default encoding! " + DEFAULT_ENCODING, e);
        }
    }

    public static String trimLines(String text) {
        String[] splitByLines = splitByLines(StringUtils.replace(text, CARRIAGE_TAB_SYMBOL, NEW_LINE_SYMBOL));
        return trimLinesAndJoin(Arrays.asList(splitByLines));
    }

    public static String trimLinesAndJoin(Collection<String> lines) {
        return join(lines, new TrimAndChompTransformer());
    }

    public static String join(Collection<String> lines) {
        return join(lines, new ChompTransformer());
    }

    public static String removeRemarks(String text) {
        String[] lines = splitByLines(text);
        return join(Arrays.asList(lines), new RemoveRemarksTransformer());
    }

    public static String[] removeRemarksAndSplit(String text) {
        String[] lines = splitByLines(text);

        List<String> res = new ArrayList<String>(Arrays.asList(lines));
        CollectionUtils.filter(res, new RemoveRemarksTransformer());

        return res.toArray(new String[res.size()]);
    }

    @SuppressWarnings("unchecked")
    private static String join(Collection<String> lines, Transformer transformer) {
        Collection<String> lineCollection = CollectionUtils.collect(lines, transformer);
        CollectionUtils.filter(lineCollection, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                String value = (String) object;
                return !StringUtils.isBlank(value);
            }
        });
        return StringUtils.join(lineCollection, StringUtil.END_LINE_SYMBOL);
    }

    /**
     * Split string by \n or \r\n separators.
     * All empty token are omitted.
     * All tokens also trimmed.
     * @param strArray test to split
     * @return array of tokens
     */
    public static String[] splitAndTrim(String strArray) {
        String[] listDst = splitByLines(strArray);
        if (listDst.length == 0) {
            return listDst;
        }

        List<String> listTgt = new ArrayList<String>(listDst.length);

        for (String s : listDst) {
            if (isPropertyNotEmpty(s)) {
                listTgt.add(s.trim());
            }
        }

        return listTgt.toArray(new String[listTgt.size()]);
    }

    public static String unquote(String string, boolean isHTMLEscaped) {
        if (isPropertyEmpty(string)) {
            return string;
        }

        string = string.trim();

        if (isHTMLEscaped) {
            if (string.startsWith("&quot;") && string.endsWith("&quot;") && string.length() > 6) {
                return string.substring(6, string.length() - 6);
            }
        } else {
            Matcher matcher = IS_UNQUOTE.matcher(string);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return string;
    }

    public static String replaceRegexp(String str, Pattern target, String replacement) {
        if (isPropertyEmpty(str)) {
            return str;
        }

        Matcher matcher = target.matcher(str);
        return matcher.replaceAll(replacement);
    }

    public static String getNumberInputMask(Locale locale, int precision, int scale) {
        String intPart = StringUtils.repeat("#", Math.max(precision - scale, 1));
        String scalePart = StringUtils.repeat("#", Math.max(scale, 0));
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(locale);
        String separator = scalePart.isEmpty() ? "" : String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator());
        return intPart + separator + scalePart;
    }

    public static String spaceToNbsp(String str) {
        if (str == null) {
            return null;
        }
        return str.replace(' ', '\u00A0');
    }

    public static long trimmedUTF8length(String value) {
        try {
            return StringUtils.trimToEmpty(value).getBytes(DEFAULT_ENCODING).length;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String addComma(boolean isAppendComma, String appendStr) {
        return (isAppendComma ? ", " + appendStr : appendStr);
    }

    public static String replicate(char ch, int count) {
        if (count <= 0) {
            return "";
        }

        char[] buf = new char[count];
        Arrays.fill(buf, ch);

        return new String(buf);
    }

    private static final class TrimAndChompTransformer implements Transformer {
        @Override
        public Object transform(Object input) {
            String value = (String) input;
            if (value != null) {
                value = StringUtils.chomp(StringUtils.trim(value));
            }
            return value;
        }
    }

    private static final class ChompTransformer implements Transformer {
        @Override
        public Object transform(Object input) {
            String value = (String) input;
            if (value != null) {
                value = StringUtils.chomp(value);
            }
            return value;
        }
    }

    public static String resolveGlobal(String resource, String id, boolean prepare) {
        return resolveGlobal(resource, id, prepare, getLocale());
    }

    public static String resolveGlobal(String resource, String id, boolean prepare, Locale locale) {
        String preparedId = prepare ? MessageHelper.prepareMessageKey(id) : id;
        String key = "global." + resource + "." + preparedId +".name";

        return getLocalizedString(key, locale);
    }

    /**
     * Converts the source string into lower case and trims in a safety way.
     *
     * @param src - string to me converted into trimmed lower case.
     * @return null if src is null, and src.trim().toLowerCase() otherwise.
     */
    public static String trimAndLower(String src) {
        if (src == null) {
            return null;
        }
        return src.trim().toLowerCase();
    }

    public static String extractUrlFromTrigger(String trigger, boolean isHTMLEscaped) {
        String clickable = trigger;
        if (clickable.startsWith("-")) {
            clickable = clickable.substring(1);
        }
        clickable = unquote(clickable, isHTMLEscaped);
        return clickable;
    }

    public static String prepareForXML(String text) {
        return BANNED_FOR_XML.matcher(text).replaceAll(" ");
    }

    private static final class RemoveRemarksTransformer implements Transformer, Predicate {
        @Override
        public Object transform(Object input) {
            if (evaluate(input)) {
                return input;
            } else {
                return "";
            }
        }

        @Override
        public boolean evaluate(Object value) {
            String str = (String) value;
            return str != null && !str.trim().isEmpty() && !str.trim().startsWith("--");
        }
    }

    private static String getLocalizedStringCachePrefix() {
        String currentCustomization = CustomizationHelper.getCustomizationName();
        if (currentCustomization == null) {
            return "";
        }
        return "customization_" + currentCustomization + "_";
    }
}

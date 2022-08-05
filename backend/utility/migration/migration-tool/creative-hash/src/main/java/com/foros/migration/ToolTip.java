package com.foros.migration;

import com.foros.model.creative.TextCreativeOption;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.apache.commons.collections.map.MultiKeyMap;

public class ToolTip {
    private static final String APPLICATION_RESOURCE = "applicationResource";
    public static final Collection<String> LANGS = Collections.unmodifiableCollection(Arrays.asList("ja", "ko", "pt", "ro", "ru", "tr", "zh", "en"));
    private static MultiKeyMap LOCALIZE_VALUES;
    private static Properties DEFAULT_PROPERTIES;

    public static MultiKeyMap getToolTips() throws IOException {
        if (LOCALIZE_VALUES == null) {
            LOCALIZE_VALUES = new MultiKeyMap();
            for (String lang : LANGS) {
                fillLocalizeValues(LOCALIZE_VALUES, lang);

            }
        }
        return LOCALIZE_VALUES;
    }

    private static void fillLocalizeValues(MultiKeyMap keyMap, String lang) throws IOException {
        Properties properties = new Properties();
        String fileName = APPLICATION_RESOURCE;
        if (!lang.equals("en")) {
            fileName += "_" + lang;
            properties.load(resource.BaseResourceBundle.class.getResourceAsStream(fileName + ".properties"));
        } else {
            properties = getDefaultProperties();
        }
        String maxSize = getProperty(properties, "creative.option.maxSizeCJK");
        keyMap.put(TextCreativeOption.HEADLINE, lang, getProperty(properties, "textAd.headline.tooltip") + ". " + maxSize.replaceAll("\\{0\\}", "25"));
        keyMap.put(TextCreativeOption.DESCRIPTION_LINE_1, lang, getProperty(properties, "textAd.descriptionLine1.tooltip") + ". " + maxSize.replaceAll("\\{0\\}", "35"));
        keyMap.put(TextCreativeOption.DESCRIPTION_LINE_2, lang, getProperty(properties, "textAd.descriptionLine2.tooltip") + ". " + maxSize.replaceAll("\\{0\\}", "35"));
        keyMap.put(TextCreativeOption.DESCRIPTION_LINE_3, lang, getProperty(properties, "textAd.descriptionLine3.tooltip") + ". " + maxSize.replaceAll("\\{0\\}", "35"));
        keyMap.put(TextCreativeOption.DESCRIPTION_LINE_4, lang, getProperty(properties, "textAd.descriptionLine4.tooltip") + ". " + maxSize.replaceAll("\\{0\\}", "35"));
        keyMap.put(TextCreativeOption.DISPLAY_URL, lang, getProperty(properties, "textAd.displayUrl.tooltip") + ". " + maxSize.replaceAll("\\{0\\}", "35"));
        keyMap.put(TextCreativeOption.CLICK_URL, lang, getProperty(properties, "textAd.clickUrl.tooltip") + ". " + maxSize.replaceAll("\\{0\\}", "1024"));
        keyMap.put(TextCreativeOption.IMAGE_FILE, lang, getProperty(properties, "textAd.imageFile.tooltip"));
    }

    private static String getProperty(Properties properties, String key) throws IOException {
        String value = properties.getProperty(key);
        if (value == null) {
            value = getDefaultValue(key);
        }
        return value;
    }

    private static String getDefaultValue(String key) throws IOException {
        Properties properties = getDefaultProperties();
        return properties.getProperty(key);
    }

    private static Properties getDefaultProperties() throws IOException {
        if (DEFAULT_PROPERTIES == null) {
            DEFAULT_PROPERTIES = new Properties();
            DEFAULT_PROPERTIES.load(resource.BaseResourceBundle.class.getResourceAsStream(APPLICATION_RESOURCE + ".properties"));
        }
        return DEFAULT_PROPERTIES;
    }

}

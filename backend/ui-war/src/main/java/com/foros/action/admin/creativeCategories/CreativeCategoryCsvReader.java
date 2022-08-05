package com.foros.action.admin.creativeCategories;

import com.foros.model.security.Language;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.UploadContext;
import com.foros.session.creative.CreativeCategoryTO;
import com.foros.util.StringUtil;
import com.foros.util.csv.CsvFormatException;
import com.foros.util.csv.CsvReader;
import com.foros.validation.interpolator.MessageInterpolator;
import com.foros.validation.interpolator.StringUtilsMessageInterpolator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class CreativeCategoryCsvReader {
    private final CsvReader reader;
    private final CreativeCategoryFieldCsvHelper helper;

    private UploadContext currentStatus;

    private CreativeCategoryCsvReader(CsvReader reader, CreativeCategoryFieldCsvHelper helper) {
        this.reader = reader;
        this.helper = helper;
    }

    private void addError(int lineNumber, CreativeCategoryFieldCsv field, String key) {
        currentStatus.addError(key, field.getFieldPath(), lineNumber);
    }

    public UploadContext getCurrentStatus() {
        return currentStatus;
    }

    public List<CreativeCategoryTO> parse() throws IOException {
        List<CreativeCategoryTO> creativeCategories = new LinkedList<>();
        Locale locale = CurrentUserSettingsHolder.getLocale();
        MessageInterpolator interpolator = new StringUtilsMessageInterpolator(locale);

        currentStatus = new UploadContext();
        try {
            while (reader.readRecord()) {
                if (reader.getColumnCount() > helper.getAllColumns().size()) {
                    currentStatus.addError("CreativeCategory.errors.incorrect.columns", reader.getLineNumber());
                    currentStatus.flush(interpolator);
                    continue;
                }

                CreativeCategoryTO creativeCategory = readCreativeCategoryTO();
                currentStatus.flush(interpolator);
                creativeCategories.add(creativeCategory);
            }
        } catch (CsvFormatException ex) {
            currentStatus.addError("CreativeCategory.errors.wrongCSV", reader.getLineNumber());
        }

        return creativeCategories;
    }

    private CreativeCategoryTO readCreativeCategoryTO() throws IOException {
        CreativeCategoryTO creativeCategoryTO = new CreativeCategoryTO();
        creativeCategoryTO.setName(readString(helper.getName(), true));
        creativeCategoryTO.setId(readLong(helper.getId()));

        List<String> rtbCategories = new ArrayList<>();

        for (CreativeCategoryFieldCsv fieldCsv : helper.getRtbKeys()) {
            rtbCategories.add(readString(fieldCsv));
        }
        creativeCategoryTO.setRtbCategories(rtbCategories);

        creativeCategoryTO.setLocalisationMap(readLocalisationMap(helper.getLocalisation()));

        return creativeCategoryTO;
    }

    private Map<String, String> readLocalisationMap(CreativeCategoryFieldCsv column) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (Language lang : Language.values()) {
            map.put(lang.getIsoCode(), "");
        }
        String str = readString(column);
        if (str != null) {
            StringTokenizer st = new StringTokenizer(str, "=;");
            try {
                while(st.hasMoreTokens()) {
                    String lang = st.nextToken().trim().toLowerCase();
                    String message = st.nextToken();
                    if (!map.containsKey(lang)) {
                        currentStatus.addError("CreativeCategory.errors.unsupportedLang", reader.getLineNumber());
                    } else {
                        map.put(lang, message);
                    }
                }
            } catch (Exception ex) {
                addError(reader.getLineNumber(), column, "CreativeCategory.errors.parseline");
                return null;
            }
            return map;
        }
        return map;
    }

    private Long readLong(CreativeCategoryFieldCsv column) throws IOException {
        String str = readString(column);
        if (StringUtils.isNotBlank(str)) {
            try {
                return Long.parseLong(str);
            } catch (Exception e) {
                addError(reader.getLineNumber(), column, "CreativeCategory.errors.parseline");
                return null;
            }
        } else {
            return null;
        }
    }

    private String readString(CreativeCategoryFieldCsv column) throws IOException {
        return readString(column, false);
    }

    private String readString(CreativeCategoryFieldCsv column, boolean required) throws IOException {
        int index = helper.getAllColumns().indexOf(column);
        boolean doRead = index != -1 && index < reader.getColumnCount();
        String str = doRead ? StringUtil.trimProperty(reader.get(index)) : null;
        if (StringUtil.isPropertyEmpty(str) && required) {
            addError(reader.getLineNumber(), column, "CreativeCategory.errors.parseline");
        }
        return "".equals(str) ? null : str;
    }

    public static class Factory {

        @Autowired
        private CreativeCategoryFieldCsvHelper helper;

        public CreativeCategoryCsvReader newInstance(CsvReader reader) {
            return new CreativeCategoryCsvReader(reader, helper);
        }

    }
}

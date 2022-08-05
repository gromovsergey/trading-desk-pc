package com.foros.session.channel.service;

import com.foros.model.channel.Channel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.trigger.PageKeywordsHolder;
import com.foros.model.channel.trigger.SearchKeywordsHolder;
import com.foros.model.channel.trigger.TriggersHolder;
import com.foros.session.BusinessException;
import com.foros.util.StringUtil;
import com.foros.util.UploadUtils;
import com.foros.util.csv.BufferedLineReader;
import com.foros.util.csv.CsvFormatException;
import com.foros.util.csv.CsvReader;
import com.foros.util.csv.CsvWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.io.IOUtils;

/**
 * Helper class to use in DiscoverSericeBean
 */
public final class DiscoverChannelUtils {

    public enum CsvColumns {
        BASE_KEYWORD(0, 1, true, false, "DiscoverChannelList.errors.emptyBaseKeyword"),
        POSITIVE_PAGE_KEYWORD(1, 5, false, true, ""),
        NEGATIVE_PAGE_KEYWORD(2, 5, false, false, ""),
        POSITIVE_SEARCH_KEYWORD(3, 5, false, true, ""),
        NEGATIVE_SEARCH_KEYWORD(4, 5, false, false, ""),
        DISCOVER_QUERY(5, 6, true, true, "DiscoverChannelList.errors.emptyDiscoverQuery");

        private int pos;
        private int requiredColumnsNum;
        private boolean required;
        private boolean macroApplicable;
        private String absenceErrorMsg;

        CsvColumns(int pos, int requiredColumnsNum, boolean required, boolean macroApplicable, String absenceErrorMsg) {
            this.pos = pos;
            this.requiredColumnsNum = requiredColumnsNum;
            this.required = required;
            this.macroApplicable = macroApplicable;
            this.absenceErrorMsg = absenceErrorMsg;
        }

        public int getPos() {
            return pos;
        }

        public int getRequiredColumnsNum() {
            return requiredColumnsNum;
        }

        public boolean isRequired() {
            return required;
        }

        public boolean isMacroApplicable() {
            return macroApplicable;
        }

        public String getAbsenceErrorMsg() {
            return absenceErrorMsg;
        }
    }

    final static private Set<Integer> permittedColumnCount;
    final static private String columnsNumberMsg;

    static {
        permittedColumnCount = new HashSet<Integer>();
        for (CsvColumns column: CsvColumns.values()) {
            permittedColumnCount.add(column.getRequiredColumnsNum());
        }

        List<Integer> asSortedList = new ArrayList<Integer>(permittedColumnCount);
        java.util.Collections.sort(asSortedList);
        StringBuilder tmp = new StringBuilder();
        for (Integer val: asSortedList) {
            if (tmp.length() != 0) {
                tmp.append(", ");
            }
            tmp.append(val.toString());
        }
        columnsNumberMsg = tmp.toString();
    }

    private DiscoverChannelUtils() {
    }

    public static void createChildChannelsFromKeywordList(DiscoverChannelList dcList) {
        Set<DiscoverChannel> childChannels = new ChannelSet();
        dcList.setChildChannels(childChannels);

        String csv = dcList.getKeywordList();
        if (StringUtils.isBlank(csv)) {
            return;
        }

        ChannelCsvReader csvReader = null;
        try {
            csvReader = new ChannelCsvReader(csv);
            while (csvReader.readRecord()) {
                DiscoverChannel child = new DiscoverChannel();
                applyListMacrosToChannel(dcList, child, csvReader);
                child.setBaseKeyword(csvReader.getRawRecord());
                childChannels.add(child); // if channel already exist we simply remove it from keywords
            }
            dcList.unregisterChange("keywordList");
        } catch (CsvFormatException e) {
            /* todo!!!!
            ValidationContext.addWrongPaths(dcList, "keywordList", "childChannels");
            */
            UploadUtils.getUploadContext(dcList).addError("errors.csv.exception")
                    .withParameters(e.getMessage())
                    .withPath("keywordList");
        } catch (IOException e) {
            // something really bad
            throw new RuntimeException(e);
        } finally {
            CsvReader.closeQuietly(csvReader);
        }
    }

    private static void makeStub(DiscoverChannel channel) {
        channel.setName(UUID.randomUUID().toString());
    }

    static void applyMacrosFromBaseKeyword(DiscoverChannelList dcList, DiscoverChannel child) {
        ChannelCsvReader csvReader = null;
        if (StringUtils.isBlank(child.getBaseKeyword())) {
            return;
        }
        try {
            csvReader = new ChannelCsvReader(child.getBaseKeyword());
            if (csvReader.readRecord()) {
                applyListMacrosToChannel(dcList, child, csvReader);
            }
            // only one line expected here
            if (csvReader.readRecord()) {
                UploadUtils.getUploadContext(child).addError("errors.field.invalid").withPath("baseKeyword");
            }
        } catch (CsvFormatException e) {
            /* todo!!!!
            ValidationContext.addWrongPaths(child,
                    "baseKeyword",
                    "name",
                    "discoverQuery",
                    "keywordList",
                    "discoverQuery",
                    "discoverAnnotation"
            );
            */
            UploadUtils.getUploadContext(child).addError("errors.csv.exception")
                    .withParameters(e.getMessage())
                    .withPath("baseKeyword");
        } catch (IOException e) {
            // something really bad
            throw new RuntimeException(e);
        } finally {
            CsvReader.closeQuietly(csvReader);
        }
    }

    private static void applyListMacrosToChannel(DiscoverChannelList dcList, DiscoverChannel child, ChannelCsvReader reader) throws IOException {
        Map<CsvColumns, String> csvColumnsValues = new HashMap<>();

        String keyword = readKeywords(dcList, child, reader, csvColumnsValues);
        if (UploadUtils.getUploadContext(child).isFatal()) {
            return;
        }

        PageKeywordsHolder pageKeywords = new PageKeywordsHolder();
        pageKeywords.setPositiveString(csvColumnsValues.get(CsvColumns.POSITIVE_PAGE_KEYWORD));
        pageKeywords.setNegativeString(csvColumnsValues.get(CsvColumns.NEGATIVE_PAGE_KEYWORD));
        if (child.getPageKeywords().isEmpty() || !TriggersHolder.equals(pageKeywords, child.getPageKeywords())) {
            child.setPageKeywords(pageKeywords);
        }

        SearchKeywordsHolder searchKeywords = new SearchKeywordsHolder();
        searchKeywords.setPositiveString(csvColumnsValues.get(CsvColumns.POSITIVE_SEARCH_KEYWORD));
        searchKeywords.setNegativeString(csvColumnsValues.get(CsvColumns.NEGATIVE_SEARCH_KEYWORD));
        if (child.getSearchKeywords().isEmpty() || !TriggersHolder.equals(searchKeywords, child.getSearchKeywords())) {
            child.setSearchKeywords(searchKeywords);
        }

        child.setDiscoverQuery(csvColumnsValues.get(CsvColumns.DISCOVER_QUERY));

        child.setName(readName(dcList, keyword));
        child.setDescription(readDescription(dcList, keyword));
        child.setDiscoverAnnotation(readAnnotation(dcList, keyword));
    }

    private static String readKeywords(DiscoverChannelList dcList, DiscoverChannel child, ChannelCsvReader reader, Map<CsvColumns, String> csvColumnsValues) {
        int columnsCount = reader.getColumnCount();

        if (!permittedColumnCount.contains(columnsCount)) {
            makeStub(child);
            UploadUtils.getUploadContext(child)
                .addFatal("DiscoverChannelList.errors.invalidColumnsCount")
                .withParameters(columnsNumberMsg)
                .withPath("childChannels[" + (reader.getLineNumber() - 1) + "]");
            return null;
        }

        boolean pageKeywordIsEmpty = true;
        boolean searchKeywordIsEmpty = true;

        String keyword = reader.get(CsvColumns.BASE_KEYWORD.getPos());
        for (CsvColumns column: CsvColumns.values()) {
            if (columnsCount >= column.getRequiredColumnsNum()) {
                if (column.isRequired() && StringUtil.isPropertyEmpty(reader.get(column.getPos()))) {
                    makeStub(child);
                    UploadUtils.getUploadContext(child).addFatal(StringUtil.getLocalizedString(column.getAbsenceErrorMsg()))
                        .withPath("childChannels[" + (reader.getLineNumber() - 1) + "]");
                    return null;
                }
            }

            String columnValue = readColumn(dcList, keyword, reader, column);
            csvColumnsValues.put(column, columnValue);

            if (StringUtil.isPropertyNotEmpty(columnValue)) {
                switch (column) {
                case POSITIVE_PAGE_KEYWORD:
                    pageKeywordIsEmpty = false;
                    break;
                case POSITIVE_SEARCH_KEYWORD:
                    searchKeywordIsEmpty = false;
                    break;
                }
            }
        }

        if (pageKeywordIsEmpty && searchKeywordIsEmpty) {
            UploadUtils.getUploadContext(child).addFatal(StringUtil.getLocalizedString("errors.atLeastOnePositiveTriggerIsRequired"))
                .withPath("childChannels[" + (reader.getLineNumber() - 1) + "]");
        }

        return keyword;
    }

    private static String readAnnotation(DiscoverChannelList dcList, String keyword) {
        return StringUtil.trimProperty(applyKeywordPattern(dcList.getDiscoverAnnotation(), keyword));
    }

    private static String readDescription(DiscoverChannelList dcList, String keyword) {
        return StringUtil.trimProperty(applyKeywordPattern(dcList.getDescription(), keyword));
    }

    private static String readName(DiscoverChannelList dcList, String keyword) {
        return StringUtil.trimProperty(applyKeywordPattern(dcList.getChannelNameMacro(), keyword));
    }

    private static String readColumn(DiscoverChannelList dcList, String keyword, ChannelCsvReader reader, CsvColumns column) {
        if (reader.getColumnCount() >= column.getRequiredColumnsNum()) {
            return reader.get(column.getPos());
        }

        if (column.isMacroApplicable()) {
            return StringUtil.trimProperty(applyKeywordPattern(
                    column == CsvColumns.DISCOVER_QUERY ? dcList.getDiscoverQuery() : dcList.getKeywordTriggerMacro(), keyword));
        }

        return null;
    }

    public static String applyKeywordPattern(String pattern, String keyword) {
        if (pattern == null) {
            return null;
        }

        return StringUtils.replace(pattern, DiscoverChannelList.KEYWORD_TOKEN, keyword);
    }

    public static void assertParsed(DiscoverChannel channel) {
        assertParsedInternal(channel, channel.getId());
    }

    public static void assertParsed(DiscoverChannelList dcList) {
        assertParsedInternal(dcList, dcList.getId());
        for (DiscoverChannel channel : dcList.getChildChannels()) {
            assertParsedInternal(channel, dcList.getId());
        }
    }

    private static void assertParsedInternal(Channel channel, Long idToReport) {
        if (UploadUtils.getUploadContext(channel).hasErrors()) {
            String msg = StringUtil.getLocalizedString("DiscoverChannel.errors.invalidCsv", idToReport.toString());
            throw new BusinessException(msg);
        }
    }
    public static String getKeywordText(DiscoverChannel channel) {
        return getKeywordText(channel.getName(), channel.getPageKeywords(), channel.getSearchKeywords(), channel.getDiscoverQuery());
    }

    public static String getKeywordText(String trigger, PageKeywordsHolder pageKeywords, SearchKeywordsHolder searchKeywords, String discoverQuery) {
        HashMap<CsvColumns, String> csvColumnsValues = new HashMap<CsvColumns, String>();
        csvColumnsValues.put(CsvColumns.BASE_KEYWORD, trigger);
        if (pageKeywords != null) {
            csvColumnsValues.put(CsvColumns.POSITIVE_PAGE_KEYWORD, pageKeywords.getPositiveString());
            csvColumnsValues.put(CsvColumns.NEGATIVE_PAGE_KEYWORD, pageKeywords.getNegativeString());
        }
        if (searchKeywords != null) {
            csvColumnsValues.put(CsvColumns.POSITIVE_SEARCH_KEYWORD, searchKeywords.getPositiveString());
            csvColumnsValues.put(CsvColumns.NEGATIVE_SEARCH_KEYWORD, searchKeywords.getNegativeString());
        }
        csvColumnsValues.put(CsvColumns.DISCOVER_QUERY, discoverQuery);

        int columnsCount = 0;
        for (CsvColumns column: CsvColumns.values()) {
            if (StringUtil.isPropertyNotEmpty(csvColumnsValues.get(column)) && columnsCount < column.getRequiredColumnsNum()) {
                columnsCount = column.getRequiredColumnsNum();
            }
        }

        List<String> channelProperties = new ArrayList<String>(3);
        for (CsvColumns column: CsvColumns.values()) {
            if (columnsCount >= column.getRequiredColumnsNum()) {
                channelProperties.add(StringUtil.trimProperty(csvColumnsValues.get(column), ""));
            }
        }

        StringWriter writer = null;
        try {
            writer = new StringWriter(1024);
            CsvWriter csvWriter = new CsvWriter(writer, ',');
            csvWriter.setAppendLineEndingChar(false);
            csvWriter.writeRecord(channelProperties.toArray(new String[channelProperties.size()]), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return writer.toString();
    }

    public static class ChannelCsvReader extends CsvReader {
        private int recordStart;
        private int recordEnd = 0;
        private int lastSeparatorsLength;
        private String str;

        public ChannelCsvReader(String str) throws IOException {
            super(new StringReader(str));
            this.str = str;
        }

        @Override
        public boolean readRecord() throws IOException {
            recordStart = recordEnd;
            return super.readRecord();
        }

        @Override
        protected BufferedLineReader createLineReader(Reader reader) throws IOException {
            return new BufferedLineReader(reader) {
                @Override
                public String readSeparators() throws IOException {
                    String separators = super.readSeparators();
                    if (separators != null) {
                        lastSeparatorsLength = separators.length();
                        recordEnd += lastSeparatorsLength;
                    } else {
                        lastSeparatorsLength = 0;
                    }
                    return separators;
                }

                @Override
                public char read() throws IOException {
                    recordEnd++;
                    return super.read();
                }
            };
        }

        public String getRawRecord() {
            return str.substring(recordStart, recordEnd - lastSeparatorsLength);
        }
    }

    // Introduce a set where channels with similar(caseless) names considered as equal
    private static class ChannelSet extends AbstractSet<DiscoverChannel> {
        private Map<String, DiscoverChannel> nameToChannel = new LinkedHashMap<String, DiscoverChannel>();

        @Override
        public boolean add(DiscoverChannel discoverChannel) {
            String key = StringUtil.trimAndLower(discoverChannel.getName());
            if (nameToChannel.containsKey(key)) {
                return false;
            }
            nameToChannel.put(key, discoverChannel);
            return true;
        }

        @Override
        public Iterator<DiscoverChannel> iterator() {
            return nameToChannel.values().iterator();
        }

        @Override
        public int size() {
            return nameToChannel.size();
        }
    }

    public static String unmacroKeyword(String pattern, String phrase) {
        int start = pattern.indexOf(DiscoverChannelList.KEYWORD_TOKEN);

        if (start == -1) {
            return phrase;
        }

        StringBuilder kw = new StringBuilder();

        for (int i = 0; i < phrase.length() - start; i++) {
            kw.append(phrase.charAt(start + i));
            if (phrase.equals(applyKeywordPattern(pattern, kw.toString()))) {
                return kw.toString();
            }
        }
        return "";
    }
}

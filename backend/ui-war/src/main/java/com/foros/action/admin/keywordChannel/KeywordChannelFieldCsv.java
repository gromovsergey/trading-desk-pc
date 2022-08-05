package com.foros.action.admin.keywordChannel;

import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.MetaDataImpl;
import com.foros.util.csv.CsvField;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum KeywordChannelFieldCsv implements CsvField {
    InternalAccount(ColumnTypes.string(), "account"),
    Keyword(ColumnTypes.string(), "name"),
    Type(ColumnTypes.string(), "triggerType"),
    Country(ColumnTypes.string(), "account.country"),
    BPFrequency(ColumnTypes.number(), "behavioralParameters.minimumVisits"),
    BPFrom(ColumnTypes.number(), "behavioralParameters.timeFrom"),
    BPTo(ColumnTypes.number(), "behavioralParameters.timeTo"),
    BPScale(ColumnTypes.string(), null),
    FCPeriod(ColumnTypes.timeSpan(), "frequencyCap.periodSpan"),
    FCWindowLimit(ColumnTypes.number(), "frequencyCap.windowCount"),
    FCWindowLength(ColumnTypes.timeSpan(), "frequencyCap.windowLengthSpan"),
    FCLife(ColumnTypes.number(), "frequencyCap.lifeCount");

    private ColumnType type;
    private String path;

    KeywordChannelFieldCsv(ColumnType type, String path) {
        this.type = type;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public ColumnType getType() {
        return type;
    }

    @Override
    public int getId() {
        return this.ordinal();
    }

    public int getIndex() {
        return ordinal();
    }

    public String getNameKey() {
        return "KeywordChannel.csv.column." + name();
    }

    public static final int TOTAL_COLUMNS_COUNT = KeywordChannelFieldCsv.values().length;

    private static final List<KeywordChannelFieldCsv> ALL_COLUMNS = Collections.unmodifiableList(Arrays.asList(KeywordChannelFieldCsv.values()));

    public static final MetaData<KeywordChannelFieldCsv> META_DATA = new MetaDataImpl<>(ALL_COLUMNS);

    public static KeywordChannelFieldCsv findColumn(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Null or empty path");
        }
        for (KeywordChannelFieldCsv field : KeywordChannelFieldCsv.values()) {
            if (path.equals(field.getPath())) {
                return field;
            }
        }
        return null;
    }
}

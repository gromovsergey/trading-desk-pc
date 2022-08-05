package com.foros.action.channel.bulk;

import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.util.csv.PathableCsvField;


public enum ChannelFieldCsv implements PathableCsvField {
    // entity columns
    Account(ColumnTypes.string(), Channel.class, "account.name"),
    Name(ColumnTypes.string(), Channel.class, "name"),
    Status(ColumnTypes.status(), Channel.class, "status"),
    Description(ColumnTypes.string(), Channel.class, "description"),
    Country(ColumnTypes.string(), Channel.class, "country"),
    Visibility(ColumnTypes.string(), Channel.class, "visibility"),

    // BehavioralChannel
    Url(ColumnTypes.string(), BehavioralChannel.class, "urls.positive"),
    UrlNegative(ColumnTypes.string(), BehavioralChannel.class, "urls.negative"),

    UrlCount(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[U].minimumVisits"),
    UrlFrom(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[U].timeFrom"),
    UrlTo(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[U].timeTo"),
    UrlUnit(ColumnTypes.string(), BehavioralChannel.class, "urlUnit"),

    SearchKeyword(ColumnTypes.string(), BehavioralChannel.class, "searchKeywords.positive"),
    SearchKeywordNegative(ColumnTypes.string(), BehavioralChannel.class, "searchKeywords.negative"),

    SearchKeywordCount(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[S].minimumVisits"),
    SearchKeywordFrom(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[S].timeFrom"),
    SearchKeywordTo(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[S].timeTo"),
    SearchKeywordUnit(ColumnTypes.string(), BehavioralChannel.class, "searchKeywordUnit"),

    PageKeyword(ColumnTypes.string(), BehavioralChannel.class, "pageKeywords.positive"),
    PageKeywordNegative(ColumnTypes.string(), BehavioralChannel.class, "pageKeywords.negative"),

    PageKeywordCount(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[P].minimumVisits"),
    PageKeywordFrom(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[P].timeFrom"),
    PageKeywordTo(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[P].timeTo"),
    PageKeywordUnit(ColumnTypes.string(), BehavioralChannel.class, "pageKeywordUnit"),

    UrlKeyword(ColumnTypes.string(), BehavioralChannel.class, "urlKeywords.positive"),
    UrlKeywordNegative(ColumnTypes.string(), BehavioralChannel.class, "urlKeywords.negative"),

    UrlKeywordCount(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[R].minimumVisits"),
    UrlKeywordFrom(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[R].timeFrom"),
    UrlKeywordTo(ColumnTypes.number(), BehavioralChannel.class, "behavioralParameters[R].timeTo"),
    UrlKeywordUnit(ColumnTypes.string(), BehavioralChannel.class, "urlKeywordUnit"),

    //Expression Channel
    Expression(ColumnTypes.string(), ExpressionChannel.class, "expression"),
    // review columns
    ValidationStatus(ColumnTypes.string()),
    Errors(ColumnTypes.string());

    public static final int TOTAL_COLUMNS_COUNT = ChannelFieldCsv.values().length;

    private ColumnType type;
    private Class beanType;
    private String fieldPath;

    ChannelFieldCsv(ColumnType type) {
        this.type = type;
    }

    ChannelFieldCsv(ColumnType type, Class beanType, String fieldPath) {
        this.type = type;
        this.beanType = beanType;
        this.fieldPath = fieldPath;
    }

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public ColumnType getType() {
        return type;
    }

    @Override
    public Class getBeanType() {
        return beanType;
    }

    @Override
    public String getFieldPath() {
        return fieldPath;
    }

    @Override
    public String getNameKey() {
        return "channel.csv.column." + name();
    }
}

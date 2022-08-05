package com.foros.reporting.meta;

import com.foros.session.reporting.parameters.Order;

public class ColumnTypes {

    private static final ColumnType ID = new ColumnTypeImpl("id");
    private static final ColumnType NUMBER = new ColumnTypeImpl("number", Order.DESC);
    private static final ColumnType STRING = new ColumnTypeImpl("string");
    private static final ColumnType LINK = new ColumnTypeImpl("link");
    private static final ColumnType DATE = new ColumnTypeImpl("date");
    private static final ColumnType DATE_TIME = new ColumnTypeImpl("dateTime");
    private static final ColumnType DAY_OF_WEEK = new ColumnTypeImpl("dayOfWeek");
    private static final ColumnType WEEK = new ColumnTypeImpl("week");
    private static final ColumnType MONTH = new ColumnTypeImpl("month");
    private static final ColumnType QUARTER = new ColumnTypeImpl("quarter");
    private static final ColumnType YEAR = new ColumnTypeImpl("year");
    private static final ColumnType CURRENCY = new ColumnTypeImpl("currency", Order.DESC);
    private static final ColumnType PERCENTS = new ColumnTypeImpl("percents", Order.DESC);
    private static final ColumnType STATUS = new ColumnTypeImpl("status");
    private static final ColumnType APPROVAL_STATUS = new ColumnTypeImpl("approvalStatus");
    private static final ColumnType TIME_SPAN = new ColumnTypeImpl("timeSpan");
    private static final ColumnType COUNTRY = new ColumnTypeImpl("country");
    private static final ColumnType BOOLEAN = new ColumnTypeImpl("boolean");
    private static final ColumnType OBJECT_TYPE = new ColumnTypeImpl("objectType");
    private static final ColumnType ACTION_TYPE = new ColumnTypeImpl("actionType");
    private static final ColumnType RESULT_TYPE = new ColumnTypeImpl("resultType");
    private static final ColumnType KEYWORD_TYPE = new ColumnTypeImpl("keywordType");

    private ColumnTypes() {
    }

    public static ColumnType id() {
        return ID;
    }

    public static ColumnType number() {
        return NUMBER;
    }

    public static ColumnType string() {
        return STRING;
    }

    public static ColumnType link() {
        return LINK;
    }

    public static ColumnType date() {
        return DATE;
    }

    public static ColumnType dateTime() {
        return DATE_TIME;
    }

    public static ColumnType week() {
        return WEEK;
    }

    public static ColumnType dayOfWeek() {
        return DAY_OF_WEEK;
    }

    public static ColumnType month() {
        return MONTH;
    }

    public static ColumnType quarter() {
        return QUARTER;
    }

    public static ColumnType year() {
        return YEAR;
    }

    public static ColumnType currency() {
        return CURRENCY;
    }

    public static ColumnType percents() {
        return PERCENTS;
    }

    public static ColumnType status() {
        return STATUS;
    }

    public static ColumnType qaStatus() {
        return APPROVAL_STATUS;
    }

    public static ColumnType timeSpan() {
        return TIME_SPAN;
    }

    public static ColumnType country() {
        return COUNTRY;
    }
    
    public static ColumnType bool() {
        return BOOLEAN;
    }

    public static ColumnType objectType(){
        return OBJECT_TYPE;
    }

    public static ColumnType actionType(){
        return ACTION_TYPE;
    }

    public static ColumnType resultType(){
        return RESULT_TYPE;
    }

    public static ColumnType keywordType(){
        return KEYWORD_TYPE;
    }
}

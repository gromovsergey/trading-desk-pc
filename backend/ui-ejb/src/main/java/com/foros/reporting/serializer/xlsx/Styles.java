package com.foros.reporting.serializer.xlsx;

public final class Styles {
    private static final String TEXT = "text";
    private static final String TEXT_ALIGN_RIGHT = "text.right";
    private static final String NUMBER = "number";
    private static final String ID = "id";
    private static final String HEADER = "header";
    private static final String PERCENT = "percent";
    private static final String DATE = "date";
    private static final String DATE_TIME = "dateTime";
    private static final String DATE_MONTH = "date.month";
    private static final String DATE_YEAR = "date.year";
    private static final String CURRENCY = "currency";
    private static final String TITLE = "title";
    private static final String PARAMETER_NAME = "parameterName";
    private static final String PARAMETER_VALUE = "parameterValue";
    private static final String SUBTOTAL = "subtotal";
    private static final String HYPERLINK = "hyperlink";
    private static final String ERROR = "error";

    public static String text() {
        return TEXT;
    }

    public static String textAlignRight() {
        return TEXT_ALIGN_RIGHT;
    }

    public static String hyperlink() {
        return HYPERLINK;
    }

    public static String number() {
        return NUMBER;
    }

    public static String number(int fractionDigits) {
        if (fractionDigits <= 0 || fractionDigits > ExcelStylesRegistry.MAX_FRACTION_DIGITS) {
            return NUMBER;
        }
        return NUMBER + "." + fractionDigits;
    }

    public static String id() {
        return ID;
    }

    public static String header() {
        return HEADER;
    }

    public static String percent() {
        return PERCENT;
    }

    public static String date() {
        return DATE;
    }

    public static String dateTime() {
        return DATE_TIME;
    }

    public static String dateMonth() {
        return DATE_MONTH;
    }

    public static String dateYear() {
        return DATE_YEAR;
    }

    public static String currency(String code) {
        return CURRENCY + "." + code;
    }

    public static String title() {
        return TITLE;
    }

    public static String parameterName() {
        return PARAMETER_NAME;
    }

    public static String parameterValue() {
        return PARAMETER_VALUE;
    }

    public static String subtotal() {
        return SUBTOTAL;
    }

    public static String error() {
        return ERROR;
    }
}

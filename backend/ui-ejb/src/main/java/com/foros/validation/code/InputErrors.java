package com.foros.validation.code;

public enum InputErrors implements ForosError {
    // UI
    UI_PARSE_ERROR(110000),

    // API
    API_ERROR(120000),

    // Xml structure errors
    XML_PARSE_ERROR(120100),
    XML_ILL_FORMED(120101, "Well-Formed XML Documents", "http://www.w3.org/TR/2006/REC-xml11-20060816/#sec-well-formed"),
    XML_UNEXPECTED_COLLECTION(120102, "Max occur for the element is one"),
    XML_WRONG_TAG(120103, "tag is not expected in this position"),

    // Date
    XML_DATE_TIME_PARSE_ERROR(120200),
    // xs:date
    XML_DATE_INVALID(120201, "xs:date", "http://www.w3.org/TR/xmlschema-2/#date"),
    // xs:time
    XML_TIME_INVALID(120202, "xs:time", "http://www.w3.org/TR/xmlschema-2/#time"),
    // xs:dateTime
    XML_DATE_TIME_INVALID(120203, "xs:dateTime", "http://www.w3.org/TR/xmlschema-2/#dateTime"),

    // Number
    XML_NUMBER_PARSE_ERROR(120300),
    // xs:decimal
    XML_DECIMAL_INVALID(120301, "xs:decimal", "http://www.w3.org/TR/xmlschema-2/#decimal"),
    // xs:int
    XML_INTEGER_INVALID(120302, "xs:integer", "http://www.w3.org/TR/xmlschema-2/#integer"),
    // xs:long
    XML_LONG_INVALID(120303, "integer from -9223372036854775808 to 9223372036854775807"),

    // Boolean (xs:boolean)
    XML_BOOLEAN_ERROR(120400, "xs:boolean", "http://www.w3.org/TR/xmlschema-2/#boolean"),

    // ID (oui:entity-id)
    XML_ID_PARSE_ERROR(120500, "integer from -9223372036854775808 to 9223372036854775807"),

    // Enum (xs:enumeration)
    XML_ENUM_PARSE_ERROR(120600, "xs:enumeration", "http://www.w3.org/TR/xmlschema-2/#dt-enumeration"),

    // Query parameter error
    PARAMETER_PARSE_ERROR(129100),

    // CSV/TSV (Bulk)
    CSV_PARSE_ERROR(130000);

    private int code;
    private String text;
    private String url;

    InputErrors(int code) {
        this(code, null, null);
    }

    InputErrors(int code, String text) {
        this(code, text, null);
    }

    InputErrors(int code, String text, String url) {
        this.code = code;
        this.text = text;
        this.url = url;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getText() {
        return text;
    }
}

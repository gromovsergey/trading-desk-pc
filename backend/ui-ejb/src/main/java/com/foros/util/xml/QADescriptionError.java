package com.foros.util.xml;

public class QADescriptionError implements QADescription {
    public static final QADescription INSTANCE = new QADescriptionError();

    @Override
    public String toString() {
        return "[QADescriptionError]";
    }
}

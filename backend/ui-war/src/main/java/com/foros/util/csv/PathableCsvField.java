package com.foros.util.csv;

public interface PathableCsvField extends CsvField {

    Class getBeanType();

    String getFieldPath();

}

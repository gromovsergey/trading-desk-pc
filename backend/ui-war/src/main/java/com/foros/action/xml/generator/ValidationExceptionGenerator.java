package com.foros.action.xml.generator;

import com.foros.util.xml.XmlUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 15:24:43
 * Version: 1.0
 */
public class ValidationExceptionGenerator implements Generator<Object> {
    private Map<String, List<String>> fieldErrors;
    private Collection<String> actionErrors;
    private Collection<String> actionMessages;

    public ValidationExceptionGenerator(Map fieldErrors, Collection actionErrors, Collection actionMessages) {
        this.fieldErrors = fieldErrors;
        this.actionErrors = actionErrors;
        this.actionMessages = actionMessages;
    }

    public String generate(Object o) {
        StringBuilder xml = new StringBuilder(Constants.XML_HEADER);

        xml.append("<error type='validation'>");

        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            xml.append("<fields>");
            for (Map.Entry<String, List<String>> field : fieldErrors.entrySet()) {
                xml.append(XmlUtil.Generator.tag("field", field.getKey(), printValue(field)));
            }
            xml.append("</fields>");
        }

        // todo other messages

        xml.append("</error>");

        return xml.toString();
    }

    private String printValue(Map.Entry<String, List<String>> field) {
        StringBuilder buffer = new StringBuilder();
        for (String value: field.getValue()) {
            buffer.append(value + " ");
        }
        return buffer.toString();
    }

}
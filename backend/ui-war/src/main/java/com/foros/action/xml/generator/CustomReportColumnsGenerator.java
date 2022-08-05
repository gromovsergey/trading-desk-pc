package com.foros.action.xml.generator;

import com.foros.util.NameValuePair;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class CustomReportColumnsGenerator implements Generator<Map<String, Collection<NameValuePair<String, String>>>> {

    public String generate(Map<String, Collection<NameValuePair<String, String>>> columns) {

        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<reportColumns>");
        for (Entry<String, Collection<NameValuePair<String, String>>> entry : columns.entrySet()) {
            xml.append("<" + entry.getKey() + "> ");
            for (NameValuePair<String, String> reportColumn : entry.getValue()) {
                xml.append("<reportColumn  ").
                        append("key=\"").append(reportColumn.getName()).append("\" ").
                        append("value=\"").append(reportColumn.getValue()).append("\" />");
            }
            xml.append("</" + entry.getKey() + "> ");
        }
        xml.append("</reportColumns>");

        return xml.toString();
    }

}

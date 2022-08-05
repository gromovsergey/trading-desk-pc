package com.foros.action.xml.generator;


import com.foros.reporting.meta.DbColumn;

import java.util.Collection;
import java.util.Map;

public class AdvertiserReportCheckboxGenerator implements Generator<Map<String, Collection<DbColumn>>>{
    public String generate(Map<String, Collection<DbColumn>> columns) {

        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<reportCheckboxes>");
        for (Map.Entry<String, Collection<DbColumn>> entry : columns.entrySet()) {
            xml.append("<" + entry.getKey() + "> ");
            for (DbColumn reportColumn : entry.getValue()) {
                xml.append("<reportColumn  ").
                        append("key=\"").append(reportColumn.getNameKey()).append("\" /> ");
            }
            xml.append("</" + entry.getKey() + "> ");
        }
        xml.append("</reportCheckboxes>");

        return xml.toString();
    }

}

package com.foros.action.xml.generator;

import com.foros.util.NameValuePair;

import java.util.Collection;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 15:24:43
 * Version: 1.0
 */
public class ReportColumnsGenerator implements Generator<Collection<NameValuePair<String, String>>> {

    public String generate(Collection<NameValuePair<String, String>> columns) {

        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<reportColumns>");

        for (NameValuePair<String, String> reportColumn : columns) {
            xml.append("<reportColumn  ").
                    append("key=\"").append(reportColumn.getName()).append("\" ").
                    append("value=\"").append(reportColumn.getValue()).append("\" />");
        }

        xml.append("</reportColumns>");

        return xml.toString();
    }

}
package com.foros.action.xml.generator;

import com.foros.util.NameValuePair;
import com.foros.util.xml.XmlUtil;

import java.util.Collection;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 14:59:21
 * Version: 1.0
 */
public class OptionsGenerator implements Generator<Collection<NameValuePair<String, String>>> {

    public String generate(Collection<NameValuePair<String, String>> options) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<options>");

        if (options != null && !options.isEmpty()) {
            for (NameValuePair<String, String> pair : options) {
                xml.append(XmlUtil.Generator.tag("option", pair.getValue(), pair.getName()));
            }
        }

        xml.append("</options>");

        return xml.toString();
    }

}

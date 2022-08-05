package com.foros.action.xml.generator;

import com.foros.util.xml.XmlUtil;

import java.util.Collection;

public class SimpleCollectionResultGenerator implements Generator<Collection<Object>> {
    @Override
    public String generate(Collection<Object> model) {
         StringBuffer xml = new StringBuffer(Constants.XML_HEADER);
        xml.append("<options>");

        if (model != null && !model.isEmpty()) {
            for (Object item : model) {
                xml.append(XmlUtil.Generator.tag("option", String.valueOf(item)));
            }
        }
        xml.append("</options>");
        return xml.toString();
    }
}

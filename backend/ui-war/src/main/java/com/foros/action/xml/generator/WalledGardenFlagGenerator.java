package com.foros.action.xml.generator;

import com.foros.util.xml.XmlUtil;

public class WalledGardenFlagGenerator implements Generator<Boolean> {

    public String generate(Boolean walledGardenFlag) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<walledGardenFlag>").
                append(XmlUtil.Generator.tag("walledGardenFlagValue", walledGardenFlag)).
                append("</walledGardenFlag>");

        return xml.toString();
    }

}

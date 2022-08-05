package com.foros.action.xml.generator;

import com.foros.model.creative.CreativeCategory;
import com.foros.util.xml.XmlUtil;

import java.util.List;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 15:24:43
 * Version: 1.0
 */
public class CreativeExcludeTagsGenerator implements Generator<List<CreativeCategory>> {

    public String generate(List<CreativeCategory> tags) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<tags>");
        for (CreativeCategory tag : tags) {
            xml.append("<tag>").append(XmlUtil.Generator.tag("name", tag.getDefaultName().toLowerCase())).append("</tag>");
        }

        xml.append("</tags>");

        return xml.toString();
    }

}
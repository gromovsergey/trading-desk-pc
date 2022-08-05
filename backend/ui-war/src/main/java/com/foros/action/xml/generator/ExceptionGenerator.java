package com.foros.action.xml.generator;

import com.foros.util.xml.XmlUtil;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 15:24:43
 * Version: 1.0
 */
public class ExceptionGenerator implements Generator<Exception> {

    public String generate(Exception e) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<error ").
                append("type='").append(e.getClass().getName()).append("'>").
                append(XmlUtil.Generator.tag("message", e.getMessage())).
                append("</error>");

        return xml.toString();
    }

}
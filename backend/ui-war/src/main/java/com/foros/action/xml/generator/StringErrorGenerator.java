package com.foros.action.xml.generator;

import com.foros.util.xml.XmlUtil;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 15:24:43
 * Version: 1.0
 */
public class StringErrorGenerator implements Generator<Object> {

    private String error;

    public StringErrorGenerator() {
    }

    public StringErrorGenerator(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String generate(Object e) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<error ").
                append("type='unknown')'>").
                append(XmlUtil.Generator.tag("message", error)).
                append("</error>");

        return xml.toString();
    }

}
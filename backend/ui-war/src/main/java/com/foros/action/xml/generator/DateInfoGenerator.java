package com.foros.action.xml.generator;

import com.foros.action.xml.model.DateInfo;
import com.foros.util.xml.XmlUtil;

/**
 * User: Nitin Afre
 * Date: Jul 13, 2009
 * Time: 5:00:56 PM
 */
public class DateInfoGenerator implements Generator<DateInfo> {

    public String generate(DateInfo model) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<dateInfo>").
                append(XmlUtil.Generator.tag("datePart", model.getDatePart())).
                append(XmlUtil.Generator.tag("timePart", model.getTimePart())).
                append("</dateInfo>");

        return xml.toString();
    }
}

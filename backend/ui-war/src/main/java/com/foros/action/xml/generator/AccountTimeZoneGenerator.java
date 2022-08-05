package com.foros.action.xml.generator;

import com.foros.model.Timezone;
import com.foros.util.xml.XmlUtil;
import com.foros.util.DateHelper;
import java.util.TimeZone;

/**
 * @author Vladimir
 */
public class AccountTimeZoneGenerator implements Generator<Timezone> {

    public String generate(Timezone timeZone) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);
        xml.append("<account>");

        if (timeZone != null) {
            long timeZoneShift = DateHelper.getDateTimeZoneOffset(TimeZone.getTimeZone(timeZone.getKey()));

            xml.append(XmlUtil.Generator.tag("timeZoneShift", timeZoneShift));
        } else {
            xml.append(XmlUtil.Generator.tag("timeZoneShift", 0));
        }
        xml.append("</account>");

        return xml.toString();
    }

}

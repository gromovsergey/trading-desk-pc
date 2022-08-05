package com.foros.action.xml.generator;

import com.foros.model.account.TnsAdvertiser;
import com.foros.util.xml.XmlUtil;

import java.util.List;

public class TnsAdvertiserGenerator implements Generator<List<TnsAdvertiser>> {

    @Override
    public String generate(List<TnsAdvertiser> tnsAdvertisers) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<tnsAdvertisers>");
        for (TnsAdvertiser tnsAdvertiser : tnsAdvertisers) {
            xml.append("<tnsAdvertiser>").
                append(XmlUtil.Generator.tag("id", tnsAdvertiser.getId())).
                append(XmlUtil.Generator.tag("name", tnsAdvertiser.getName())).
            append("</tnsAdvertiser>");
        }

        xml.append("</tnsAdvertisers>");

        return xml.toString();
    }

}
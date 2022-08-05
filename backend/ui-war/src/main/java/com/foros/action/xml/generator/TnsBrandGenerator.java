package com.foros.action.xml.generator;

import com.foros.model.account.TnsBrand;
import com.foros.util.xml.XmlUtil;

import java.util.List;

public class TnsBrandGenerator implements Generator<List<TnsBrand>> {

    @Override
    public String generate(List<TnsBrand> tnsBrands) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<tnsBrands>");
        for (TnsBrand tnsBrand : tnsBrands) {
            xml.append("<tnsBrand>").
                append(XmlUtil.Generator.tag("id", tnsBrand.getId())).
                append(XmlUtil.Generator.tag("name", tnsBrand.getName())).
                append("</tnsBrand>");
        }

        xml.append("</tnsBrands>");

        return xml.toString();
    }
}

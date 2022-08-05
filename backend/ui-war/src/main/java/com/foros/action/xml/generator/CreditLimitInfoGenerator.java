package com.foros.action.xml.generator;

import com.foros.action.xml.model.CreditLimitInfo;
import com.foros.util.xml.XmlUtil;


public class CreditLimitInfoGenerator implements Generator<CreditLimitInfo> {

    public String generate(CreditLimitInfo creditLimitInfo) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<CreditLimitInfo>").
                append(XmlUtil.Generator.tag("status", creditLimitInfo.getStatus())).
                append(XmlUtil.Generator.tag("maxCreditLimit", creditLimitInfo.getMaxCreditLimit())).
                append(XmlUtil.Generator.tag("message", creditLimitInfo.getMessage())).
                append("</CreditLimitInfo>");

        return xml.toString();
    }
}

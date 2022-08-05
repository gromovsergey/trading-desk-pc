package com.foros.action.xml.generator;

import com.foros.action.xml.model.TriggerQACriteriaResult;
import com.foros.util.xml.XmlUtil;

public class TriggerQACriteriaValidationResultGenerator implements Generator<TriggerQACriteriaResult> {
    @Override
    public String generate(TriggerQACriteriaResult result) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<TriggerQACriteriaResult>").
                append(XmlUtil.Generator.tag("valid", result.isValid())).
                append(XmlUtil.Generator.tag("message", result.getMessage())).
                append("</TriggerQACriteriaResult>");

        return xml.toString();
    }
}

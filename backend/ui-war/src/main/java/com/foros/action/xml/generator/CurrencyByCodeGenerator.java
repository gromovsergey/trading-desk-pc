package com.foros.action.xml.generator;

import com.foros.action.xml.model.CurrencyInfo;
import com.foros.util.xml.XmlUtil;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 15:24:43
 * Version: 1.0
 */
public class CurrencyByCodeGenerator implements Generator<CurrencyInfo> {

    public String generate(CurrencyInfo currency) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<currency>").
                append(XmlUtil.Generator.tag("name", currency.getName())).
                append(XmlUtil.Generator.tag("symbol", currency.getSymbol())).
                append(XmlUtil.Generator.tag("fractionDigits", currency.getFractionDigits())).
                append("</currency>");

        return xml.toString();
    }

}
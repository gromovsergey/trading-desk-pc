package com.foros.action.xml.generator;

import com.foros.model.currency.Currency;
import com.foros.util.CurrencyHelper;
import com.foros.util.xml.XmlUtil;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 15:24:43
 * Version: 1.0
 */
public class CurrencyGenerator implements Generator<Currency> {

    public String generate(Currency currency) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<currency>").
                append(XmlUtil.Generator.tag("symbol", CurrencyHelper.getCurrencySymbol(currency.getCurrencyCode()))).
                append(XmlUtil.Generator.tag("id", currency.getId())).
                append("</currency>");

        return xml.toString();
    }

}
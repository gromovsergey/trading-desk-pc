package com.foros.jaxb.adapters;

import com.foros.util.StringUtil;

import java.math.BigDecimal;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BigDecimalXmlAdapter extends XmlAdapter<String, BigDecimal> {

    @Override
    public String marshal(BigDecimal bigDecimal) throws Exception {
        if (bigDecimal != null) {
            return bigDecimal.stripTrailingZeros().toPlainString();
        } else {
            return null;
        }
    }

    @Override
    public BigDecimal unmarshal(String s) throws Exception {
        return StringUtil.isPropertyEmpty(s) ? null : new BigDecimal(s).stripTrailingZeros();
    }
}

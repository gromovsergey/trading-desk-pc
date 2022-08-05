package app.programmatic.ui.agentreport.tool.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;

public class BigDecimalInWordsXmlAdapter extends XmlAdapter<String, BigDecimal> {

    @Override
    public String marshal(BigDecimal bigDecimal) throws Exception {
        if (bigDecimal != null) {
            return MoneyInWords.inWords(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            return null;
        }
    }

    @Override
    public BigDecimal unmarshal(String s) throws Exception {
        return null;
    }
}

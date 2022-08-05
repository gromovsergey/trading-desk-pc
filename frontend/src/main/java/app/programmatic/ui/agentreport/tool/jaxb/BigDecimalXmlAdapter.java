package app.programmatic.ui.agentreport.tool.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;

public class BigDecimalXmlAdapter extends XmlAdapter<String, BigDecimal> {

    @Override
    public String marshal(BigDecimal bigDecimal) throws Exception {
        if (bigDecimal != null) {
            return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        } else {
            return null;
        }
    }

    @Override
    public BigDecimal unmarshal(String s) throws Exception {
        return null;
    }
}
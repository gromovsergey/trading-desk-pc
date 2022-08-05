package app.programmatic.ui.agentreport.tool.jaxb;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

public class DateXmlAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public String marshal(LocalDate date) throws Exception {
        if (date == null) {
            return null;
        }

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd MMMM yyyy", LOCALE_RU);
        return date.format(format);
    }

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return null;
    }
}
package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.util.NameValuePair;
import com.foros.util.messages.MessageProvider;

import java.util.ArrayList;
import java.util.Collection;

public class ReportColumnsXmlAction extends AbstractXmlAction<Collection<NameValuePair<String, String>>> {

    private String[] reportColumns;

    //@RequiredFieldValidator(key = "errors.required", message = "reportColumns")
    public String[] getReportColumns() {
        return reportColumns;
    }

    public void setReportColumns(String[] reportColumns) {
        this.reportColumns = reportColumns;
    }

    public Collection<NameValuePair<String, String>> generateModel() throws ProcessException {
        String[] columns = getReportColumns();
        Collection<NameValuePair<String, String>> result = new ArrayList<NameValuePair<String, String>>();
        MessageProvider messageProvider = MessageProvider.createMessageProviderAdapter();

        if (columns != null) {
            for (String column : columns) {
                result.add(new NameValuePair<String, String>(column, messageProvider.getMessage(column)));
            }
        }

        return result;
    }

}

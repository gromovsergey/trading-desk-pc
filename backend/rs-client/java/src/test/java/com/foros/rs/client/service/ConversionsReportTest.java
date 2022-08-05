package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.report.DateRange;
import com.foros.rs.client.model.report.ReportFormat;
import com.foros.rs.client.model.report.conversions.ConversionsReportColumn;
import com.foros.rs.client.model.report.conversions.ConversionsReportParameters;
import com.foros.rs.client.result.RsConstraintViolationException;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class ConversionsReportTest  extends AbstractUnitTest {
    @Override
    protected void loadProps() throws IOException {
        super.loadProps();
        props.setProperty("foros.userToken", stringProperty("foros.advertiser.userToken"));
        props.setProperty("foros.key", stringProperty("foros.advertiser.key"));
    }

    @Test
    public void test() throws Exception {
        test(ReportFormat.csv);
    }

    @Test
    public void testExcel() throws Exception {
        test(ReportFormat.excel);
    }

    @Test
    public void testWithWrongDateRange() throws DatatypeConfigurationException, IOException {
        ConversionsReportParameters parameters = createParameters();
        GregorianCalendar begin = new GregorianCalendar();
        begin.set(GregorianCalendar.YEAR, begin.get(GregorianCalendar.YEAR) + 1);
        parameters.getDateRange().setBegin(newXmlDate(begin));
        try {
            execute(ReportFormat.csv, parameters);
            fail();
        } catch (RsConstraintViolationException e) {
            assertEquals(1, e.getConstraintViolations().size());
        }
    }

    @Test(expected = RsConstraintViolationException.class)
    public void testWithWrongAccountId() throws DatatypeConfigurationException, IOException {
        ConversionsReportParameters parameters = createParameters();

        // internal id instead of agency\advertiser
        parameters.setAccountId(longProperty("foros.test.internal.id"));
        execute(ReportFormat.csv, parameters);
    }

    private void test(ReportFormat format) throws DatatypeConfigurationException, IOException {
        ConversionsReportParameters parameters = createParameters();
        byte[] bytes = execute(format, parameters);
        assertEquals(bytes.length > 0, true);
    }

    private byte[] execute(ReportFormat format, ConversionsReportParameters parameters) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        reportService.processConversionsReport(parameters, format, result);

        result.flush();
        result.close();

        return result.toByteArray();
    }


    private ConversionsReportParameters createParameters() throws DatatypeConfigurationException {
        ConversionsReportParameters parameters = new ConversionsReportParameters();
        DateRange dateRange = new DateRange();

        dateRange.setBegin(newXmlDate(new GregorianCalendar()));
        dateRange.setEnd(newXmlDate(new GregorianCalendar()));
        parameters.setDateRange(dateRange);

        parameters.setAccountId(longProperty("foros.test.agency.id"));
        parameters.getColumns().addAll(new ArrayList<ConversionsReportColumn>(Arrays.asList(
                ConversionsReportColumn.DATE,
                ConversionsReportColumn.CAMPAIGN,
                ConversionsReportColumn.IMPRESSIONS,
                ConversionsReportColumn.CLICKS
        )));
        return parameters;
    }

    private XMLGregorianCalendar newXmlDate(GregorianCalendar cal) throws DatatypeConfigurationException {
        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        xmlCal.setYear(cal.get(GregorianCalendar.YEAR));
        xmlCal.setMonth(cal.get(GregorianCalendar.MONTH) + 1);
        xmlCal.setDay(cal.get(GregorianCalendar.DAY_OF_MONTH));
        return xmlCal;
    }
}

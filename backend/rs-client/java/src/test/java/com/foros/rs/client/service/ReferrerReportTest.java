package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.RsNotAuthorizedException;
import com.foros.rs.client.model.report.DateRange;
import com.foros.rs.client.model.report.ReportFormat;
import com.foros.rs.client.model.report.referrer.ReferrerReportParameters;
import com.foros.rs.client.result.RsConstraintViolationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

public class ReferrerReportTest extends AbstractUnitTest {

    @Override
    protected void loadProps() throws IOException {
        super.loadProps();
        props.setProperty("foros.userToken", stringProperty("foros.publisher.userToken"));
        props.setProperty("foros.key", stringProperty("foros.publisher.key"));
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
        ReferrerReportParameters parameters = createParameters();
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

    @Test(expected = RsNotAuthorizedException.class)
    public void testWithWrongAccountId() throws DatatypeConfigurationException, IOException {
        ReferrerReportParameters parameters = createParameters();

        // internal id instead of publisher
        parameters.setAccountId(longProperty("foros.test.internal.id"));
        execute(ReportFormat.csv, parameters);
    }

    public void testWithWrongSiteId() throws DatatypeConfigurationException, IOException {
        ReferrerReportParameters parameters = createParameters();
        parameters.setSiteId(-1L);
        try {
            execute(ReportFormat.csv, parameters);
            fail();
        } catch (RsConstraintViolationException e) {
            assertEquals(1, e.getConstraintViolations().size());
        }
    }

    private void test(ReportFormat format) throws DatatypeConfigurationException, IOException {
        ReferrerReportParameters parameters = createParameters();
        byte[] bytes = execute(format, parameters);
        assertEquals(bytes.length > 0, true);
    }

    private byte[] execute(ReportFormat format, ReferrerReportParameters parameters) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        reportService.processReferrerReport(parameters, format, result);

        result.flush();
        result.close();

        return result.toByteArray();
    }

    private ReferrerReportParameters createParameters() throws DatatypeConfigurationException {
        ReferrerReportParameters parameters = new ReferrerReportParameters();
        DateRange dateRange = new DateRange();

        dateRange.setBegin(newXmlDate(new GregorianCalendar()));
        dateRange.setEnd(newXmlDate(new GregorianCalendar()));
        parameters.setDateRange(dateRange);

        parameters.setSiteId(longProperty("foros.test.site.id"));
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

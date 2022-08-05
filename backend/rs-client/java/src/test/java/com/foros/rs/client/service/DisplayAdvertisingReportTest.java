package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.RsNotAuthorizedException;
import com.foros.rs.client.model.report.DateRange;
import com.foros.rs.client.model.report.ReportFormat;
import com.foros.rs.client.model.report.advertiser.AdvertiserReportColumn;
import com.foros.rs.client.model.report.advertiser.AdvertiserReportParameters;
import com.foros.rs.client.model.report.advertiser.DetailLevel;
import com.foros.rs.client.result.RsConstraintViolationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

public class DisplayAdvertisingReportTest extends AbstractUnitTest {

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
    public void testWithCreativeLink() throws Exception {
        AdvertiserReportParameters parameters = createParameters();
        parameters.setCreativeLinkIds(Arrays.asList(longProperty("foros.text.creativeLinkId")));
        parameters.setReportType(DetailLevel.Creative);

        byte[] bytes = execute(ReportFormat.excel, parameters);
        assertEquals(bytes.length > 0, true);
    }

    @Test
    public void testExcel() throws Exception {
        test(ReportFormat.excel);
    }

    @Test
    public void testWithWrongDateRange() throws DatatypeConfigurationException, IOException {
        AdvertiserReportParameters parameters = createParameters();
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
        AdvertiserReportParameters parameters = createParameters();

        // internal id instead of agency\advertiser
        parameters.setAccountId(longProperty("foros.test.internal.id"));
        execute(ReportFormat.csv, parameters);
    }

    @Test
    public void testWithWrongColumn() throws DatatypeConfigurationException, IOException {
        AdvertiserReportParameters parameters = createParameters();

        parameters.getColumns().add(AdvertiserReportColumn.CURRENT_CPC_BID);
        parameters.getColumns().add(AdvertiserReportColumn.MARGIN);
        try {
            execute(ReportFormat.csv, parameters);
            fail();
        } catch (RsConstraintViolationException e) {
            assertEquals(2, e.getConstraintViolations().size());
        }
    }

    private void test(ReportFormat format) throws DatatypeConfigurationException, IOException {
        AdvertiserReportParameters parameters = createParameters();
        byte[] bytes = execute(format, parameters);
        assertEquals(bytes.length > 0, true);
    }

    private byte[] execute(ReportFormat format, AdvertiserReportParameters parameters) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {

            reportService.processDisplayAdvertisingReport(parameters, format, result);

            result.flush();
            result.close();

            return result.toByteArray();
        }
    }

    private AdvertiserReportParameters createParameters() throws DatatypeConfigurationException {
        AdvertiserReportParameters parameters = new AdvertiserReportParameters();
        DateRange dateRange = new DateRange();

        dateRange.setBegin(newXmlDate(new GregorianCalendar()));
        dateRange.setEnd(newXmlDate(new GregorianCalendar()));
        parameters.setDateRange(dateRange);

        parameters.setAccountId(longProperty("foros.test.agency.id"));
        parameters.setReportType(DetailLevel.Campaign);
        parameters.setCostAndRates(null);
        parameters.getColumns().addAll(new ArrayList<>(Arrays.asList(
            AdvertiserReportColumn.CAMPAIGN,
            AdvertiserReportColumn.IMPRESSIONS
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

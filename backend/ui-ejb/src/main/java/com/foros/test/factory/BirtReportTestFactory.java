package com.foros.test.factory;

import com.foros.model.report.birt.BirtReport;
import com.foros.session.birt.BirtReportService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class BirtReportTestFactory extends TestFactory<BirtReport> {
    @EJB
    private BirtReportService birtReportService;

    public void populate(BirtReport report) {
        report.setName(getTestEntityRandomName());
    }

    @Override
    public BirtReport create() {
        BirtReport report = new BirtReport();
        populate(report);
        return report;
    }

    @Override
    public void persist(BirtReport report) {
        try {
            birtReportService.create(report, null, new ByteArrayInputStream(new byte[0]), 0L);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(BirtReport report) {
        try {
            birtReportService.update(report, null, null, false, new ByteArrayInputStream(new byte[0]), 0L);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BirtReport createPersistent() {
        BirtReport report = create();
        persist(report);
        return report;
    }
}

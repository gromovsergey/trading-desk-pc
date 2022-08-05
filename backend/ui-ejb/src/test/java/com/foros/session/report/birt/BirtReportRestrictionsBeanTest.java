package com.foros.session.report.birt;

import static com.foros.config.ConfigParameters.DEFAULT_MAX_FILES_PER_FOLDER;
import com.foros.AbstractRestrictionsBeanTest;
import com.foros.config.MockConfigService;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.security.PolicyEntry;
import com.foros.security.AccountRole;
import com.foros.session.birt.BirtReportRestrictions;
import com.foros.test.UserDefinition;
import com.foros.test.UserDefinitionFactory;
import com.foros.test.factory.BirtReportTestFactory;

import group.Db;
import group.Report;
import group.Restriction;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class, Report.class })
public class BirtReportRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private BirtReportRestrictions reportRestrictions;

    @Autowired
    private UserDefinitionFactory userDefinitionFactory;

    @Autowired
    private BirtReportTestFactory birtReportTF;

    @Autowired
    private MockConfigService configService;

    private BirtReport report;
    private UserDefinition internalThisReportOnly;
    private UserDefinition internalOtherReportOnly;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        configService.set(DEFAULT_MAX_FILES_PER_FOLDER, 1000);

        report = birtReportTF.createPersistent();

        internalThisReportOnly = userDefinitionFactory.create(AccountRole.INTERNAL);
        internalOtherReportOnly = userDefinitionFactory.create(AccountRole.INTERNAL);

        String reportId = report.getId().toString();
        internalThisReportOnly.addCustomPermission(new PolicyEntry("birt_report", "edit", reportId));
        internalThisReportOnly.addCustomPermission(new PolicyEntry("birt_report", "run", reportId));

        String otherReportId = String.valueOf(report.getId() + 1000);
        internalOtherReportOnly.addCustomPermission(new PolicyEntry("birt_report", "edit", otherReportId));
        internalOtherReportOnly.addCustomPermission(new PolicyEntry("birt_report", "run", otherReportId));
    }

    @Override
    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(advertiserAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(internalOtherReportOnly, false);
    }

    @Test
    public void testCanRunUpdate() throws Exception {
        expectResult(internalAllAccess, true);
        expectResult(internalThisReportOnly, true);

        Callable callCanUpdate = new Callable("birt_report", "edit") {
            @Override
            public boolean call() {
                return reportRestrictions.canUpdate(report.getId());
            }
        };

        Callable callCanRun = new Callable("birt_report", "run") {
            @Override
            public boolean call() {
                return reportRestrictions.canRun(report.getId(), Collections.<String, Object>emptyMap());
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(internalThisReportOnly, true);

        doCheck(callCanUpdate);
        doCheck(callCanRun);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("birt_report", "create") {
            @Override
            public boolean call() {
                return reportRestrictions.canCreate();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanCreate);
    }
}

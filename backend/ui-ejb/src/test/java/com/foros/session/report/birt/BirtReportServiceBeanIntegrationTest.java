package com.foros.session.report.birt;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.config.ConfigParameters;
import com.foros.config.MockConfigService;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.report.birt.BirtReportInstance;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.UserRole;
import com.foros.security.AccountRole;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.session.birt.BirtReportService;
import com.foros.session.birt.BirtReportTO;
import com.foros.test.factory.UserRoleTestFactory;
import com.foros.util.VersionCollisionException;

import group.Db;
import group.Report;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class BirtReportServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private BirtReportService birtReportService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRoleTestFactory userRoleTF;

    @Autowired
    private MockConfigService configService;

    private String reportName;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        reportName = userRoleTF.getTestEntityRandomName();
    }

    @Test
    public void testFindById() throws Exception  {
        BirtReport reportNEW = createBirtReport(getReportId());
        birtReportService.create(reportNEW, null, newReportStream(), 0);
        Long reportId = reportNEW.getId();

        BirtReport report = birtReportService.get(reportId);
        assertNotNull(report);
        assertEquals("Can't find Birt Report <" + reportId + ">", reportId, report.getId());
    }

    private ByteArrayInputStream newReportStream() {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Test
    public void testCreate() throws Exception {
        BirtReport report = createBirtReport(getReportId());
        birtReportService.create(report, null, newReportStream(), 0);
        assertNotNull("ID wasn't set", report.getId());

        assertEquals("BirtReport wasn't found in the DB", 1,
                jdbcTemplate.queryForInt("SELECT COUNT(*) FROM BIRTREPORT WHERE NAME = ?",
                        reportName));
    }

    @Test
    public void testDelete() throws Exception {
        BirtReport report = createBirtReport(null);
        persist(report);

        birtReportService.delete(report.getId());
        getEntityManager().flush();
        assertEquals("BirtReport  founded in the DB", 0,
                jdbcTemplate.queryForInt("select count(*) from BIRTREPORT where name = ?",
                reportName));
    }

    @Test
    public void testUpdate() throws Exception {
        BirtReport report = createBirtReport(getReportId());

        birtReportService.create(report, null, newReportStream(), 0);

        String updatedName = userRoleTF.getTestEntityRandomName();
        report.setName(updatedName);
        birtReportService.update(report, new LinkedList<UserRole>(), new LinkedList<UserRole>(), false, null, 0);

        assertEquals("BirtReport wasn't found in the DB", 1,
                jdbcTemplate.queryForInt("select count(*) from BIRTREPORT where name = ?",
                updatedName));

    }

    @Test
    public void testBirtReportAddPolicies() throws Exception {
        Map<Long, UserRole> userRoles = new HashMap<Long, UserRole>();
        Set<PolicyEntry> policiesEntry;

        BirtReport report = getBirtReport();

        UserRole role = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role, Long.toString(report.getId()), "run"));

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(1, policiesEntry.size());

        userRoles = generateUserRolesMap(policiesEntry);

        UserRole role1 = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role1, Long.toString(report.getId()), "run"));

        UserRole role2 = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role2, Long.toString(report.getId()), "run"));

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(3, policiesEntry.size());
    }

    @Test
    public void testBirtReportUpdateActionTypeOfPolicies() throws Exception {
        Map<Long, UserRole> userRoles = new HashMap<Long, UserRole>();
        Set<PolicyEntry> policiesEntry;

        BirtReport report = getBirtReport();

        UserRole role = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role, Long.toString(report.getId()), "run"));
        addPolicyToMap(userRoles, getPolicy(role, Long.toString(report.getId()), "edit"));

        UserRole role1 = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role1, Long.toString(report.getId()), "run"));

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(3, policiesEntry.size());

        userRoles = generateUserRolesMap(policiesEntry);

        addPolicyToMap(userRoles, getPolicy(role1, Long.toString(report.getId()), "edit"));

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(4, policiesEntry.size());
    }

    @Test
    public void testBirtReportAddAndDeletePolicies() throws Exception {
        Map<Long, UserRole> userRoles = new HashMap<Long, UserRole>();
        Set<PolicyEntry> policiesEntry;

        BirtReport report = getBirtReport();

        UserRole role = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role, Long.toString(report.getId()), "run"));

        UserRole role1 = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role1, Long.toString(report.getId()), "run"));

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(2, policiesEntry.size());

        entityManager.clear();

        userRoles = generateEmptyUserRolesMap(policiesEntry);
        addPolicyToMap(userRoles, policiesEntry.iterator().next());

        UserRole role2 = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role2, Long.toString(report.getId()), "run"));

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(2, policiesEntry.size());
    }

    @Test
    public void testBirtReportDeleteAndAddSamePolicy() throws Exception {
        Map<Long, UserRole> userRoles = new HashMap<Long, UserRole>();
        Set<PolicyEntry> policiesEntry;

        BirtReport report = getBirtReport();

        UserRole role = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role, Long.toString(report.getId()), "run"));

        UserRole role1 = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role1, Long.toString(report.getId()), "run"));

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(2, policiesEntry.size());

        entityManager.clear();

        userRoles = generateEmptyUserRolesMap(policiesEntry);
        Iterator<PolicyEntry> iterator = policiesEntry.iterator();
        addPolicyToMap(userRoles, iterator.next());

        PolicyEntry sameEntry = new PolicyEntry();
        copyPolicy(sameEntry, iterator.next());
        addPolicyToMap(userRoles, sameEntry);

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(2, policiesEntry.size());
    }

    @Test
    public void testBirtReportDeletePolicies() throws Exception {
        Map<Long, UserRole> userRoles = new HashMap<Long, UserRole>();
        Set<PolicyEntry> policiesEntry;

        BirtReport report = getBirtReport();

        UserRole role = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role, Long.toString(report.getId()), "run"));

        UserRole role1 = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role1, Long.toString(report.getId()), "run"));

        UserRole role2 = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role2, Long.toString(report.getId()), "run"));

        UserRole role3 = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role3, Long.toString(report.getId()), "run"));

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(4, policiesEntry.size());

        entityManager.clear();

        //delete 1
        userRoles = generateEmptyUserRolesMap(policiesEntry);
        Iterator<PolicyEntry> iterator = policiesEntry.iterator();
        addPolicyToMap(userRoles, iterator.next());
        addPolicyToMap(userRoles, iterator.next());
        addPolicyToMap(userRoles, iterator.next());

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(3, policiesEntry.size());

        //delete all
        userRoles = generateEmptyUserRolesMap(policiesEntry);

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(0, policiesEntry.size());
    }

    @Test
    public void testBirtReportUpdatePoliciesVersionCollision() throws Exception {
        Map<Long, UserRole> userRoles = new HashMap<Long, UserRole>();
        Set<PolicyEntry> policiesEntry;

        BirtReport report = getBirtReport();

        UserRole role = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role, Long.toString(report.getId()), "run"));

        UserRole role1 = getUserRole();
        addPolicyToMap(userRoles, getPolicy(role1, Long.toString(report.getId()), "run"));

        birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
        policiesEntry = new HashSet<PolicyEntry>(userRoleService.findPolicyEntries("birt_report", Long.toString(report.getId())));

        assertEquals(2, policiesEntry.size());

        userRoles = generateUserRolesMap(policiesEntry);
        userRoles.values().iterator().next().setVersion(new Timestamp(0));

        try {
            birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
            fail("Exception must be thrown!");
        } catch (VersionCollisionException ex) {
            //
        }

        userRoles = generateEmptyUserRolesMap(policiesEntry);
        userRoles.values().iterator().next().setVersion(new Timestamp(0));

        try {
            birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
            fail("Exception must be thrown!");
        } catch (VersionCollisionException ex) {
            //
        }

        userRoles.clear();
        addPolicyToMap(userRoles, policiesEntry.iterator().next());

        try {
            birtReportService.update(report, new LinkedList<UserRole>(userRoles.values()), new LinkedList<UserRole>(), false, null, 0);
            fail("Exception must be thrown!");
        } catch (VersionCollisionException ex) {
            //
        }
    }

    private BirtReport createBirtReport(Long id) throws Exception {
        if (id == null){
            id = jdbcTemplate.queryForLong("SELECT max(BIRT_REPORT_ID) FROM BIRTREPORT ") + 1 ;
        }
        BirtReport report = new BirtReport();
        report.setId(id);
        report.setName(reportName);
        return report;
    }

    private Long getReportId(){
        return jdbcTemplate.queryForLong("select nextval('BIRTREPORTINSTANCE_BIRT_REPORT_INSTANCE_ID_SEQ'::regclass)") + 1 ;
    }

    private BirtReport getBirtReport() throws Exception {
        BirtReport report = createBirtReport(getReportId());

        birtReportService.create(report, null, newReportStream(), 0);
        assertNotNull("ID wasn't set", report.getId());

        assertEquals("BirtReport wasn't found in the DB", 1,
                jdbcTemplate.queryForInt("SELECT COUNT(*) FROM BIRTREPORT WHERE NAME = ?",
                        reportName));

        return report;
    }

    private UserRole getUserRole() {
        AccountRole accountRole = AccountRole.INTERNAL;
        UserRole role = userRoleTF.createPersistent(accountRole);
        entityManager.clear();
        return role;
    }

    private PolicyEntry getPolicy(UserRole role, String id, String actionType) {
        PolicyEntry policy = new PolicyEntry();
        policy.setUserRole(role);
        policy.setType("birt_report");
        policy.setParameter(id);
        policy.setAction(actionType);
        return policy;
    }

    private void copyPolicy(PolicyEntry destinationPolicy, PolicyEntry sourcePolicy) {
        destinationPolicy.setParameter(sourcePolicy.getParameter());
        destinationPolicy.setAction(sourcePolicy.getAction());
        destinationPolicy.setType(sourcePolicy.getType());
        destinationPolicy.setUserRole(sourcePolicy.getUserRole());
    }

    private void addPolicyToMap(Map<Long, UserRole> userRoles, PolicyEntry policy) {
        Long userRoleId = policy.getUserRole().getId();
        if (userRoles.containsKey(userRoleId)) {
            UserRole userRole = userRoles.get(userRoleId);
            userRole.getPolicyEntries().add(policy);
        } else {
            UserRole userRole = new UserRole(userRoleId);
            userRole.setVersion(policy.getUserRole().getVersion());
            userRole.getPolicyEntries().add(policy);
            userRoles.put(userRoleId, userRole);
        }
    }

    private Map<Long, UserRole> generateUserRolesMap(Set<PolicyEntry> policiesEntry) {
        Map<Long, UserRole> userRoles = new HashMap<Long, UserRole>();
        for (PolicyEntry policy : policiesEntry) {
            addPolicyToMap(userRoles, policy);
        }
        return userRoles;
    }

    private Map<Long, UserRole> generateEmptyUserRolesMap(Set<PolicyEntry> policiesEntry) {
        Map<Long, UserRole> userRoles = new HashMap<Long, UserRole>();
        for (PolicyEntry policy : policiesEntry) {
            Long userRoleId = policy.getUserRole().getId();
            if (!userRoles.containsKey(userRoleId)) {
                UserRole userRole = new UserRole(userRoleId);
                userRole.setVersion(policy.getUserRole().getVersion());
                userRoles.put(userRoleId, userRole);
            }
        }
        return userRoles;
    }

    @Test
    public void testExpireInstancesAndSessions() throws Exception {
        BirtReport report = createBirtReport(getReportId());
        birtReportService.create(report, null, newReportStream(), 0);

        BirtReportInstance instance = new BirtReportInstance();
        instance.setReport(report);
        instance.setCreated(new DateTime().minusDays(1).toDate());
        instance.setDocumentFileName("test");
        instance.setParametersHash("hash");
        birtReportService.createInstance(instance);

        BirtReportSession session = birtReportService.createSession(report);
        session.setCreated(new DateTime().minusDays(1).toDate());
        session.setBirtReportInstance(instance);
        entityManager.merge(session);
        commitChanges();
        clearContext();
        session = entityManager.find(BirtReportSession.class, session.getId());

        configService.set(ConfigParameters.REPORT_SESSION_EXPIRE, 0L);
        birtReportService.expireInstancesAndSessions();
    }
}

package com.foros.session.security.auditLog;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.security.AccountRole;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import group.Db;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class SearchAuditServiceBeanTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private SearchAuditService searchAuditService;

    @Test
    public void testGetHistory() throws Exception {
        assertNotNull(searchAuditService.getHistory(ObjectType.InternalAccount, null, null, 1, 1));
        assertNotNull(searchAuditService.getHistory(ObjectType.InternalAccount, ActionType.UPDATE, 1L, 1, 1));
    }

    @Test
    public void testSearch() throws SQLException {
        AuditReportParameters parameters = new AuditReportParameters();
        parameters.setPage(1L);
        parameters.setDateFrom(new LocalDateTime().minusHours(1));
        parameters.setDateTo(new LocalDateTime());

        // minimal
        assertNotNull(searchAuditService.searchLogs(parameters));

        parameters.setAccountName("1");
        parameters.setAccountRoleIds(Arrays.asList((long) AccountRole.ADVERTISER.ordinal(), (long) AccountRole.AGENCY.ordinal()));
        parameters.setActionType(ActionType.UPDATE);
        parameters.setEmail("@");
        ArrayList<Long> types = new ArrayList<Long>();
        types.add(ObjectType.InternalAccount.getId().longValue());
        parameters.setObjectTypeIds(types);

        // full
        assertNotNull(searchAuditService.searchLogs(parameters));
    }
}

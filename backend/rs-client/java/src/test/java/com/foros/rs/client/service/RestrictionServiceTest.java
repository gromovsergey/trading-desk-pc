package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.restriction.Predicates;
import com.foros.rs.client.model.restriction.RestrictionCommand;
import com.foros.rs.client.model.restriction.RestrictionCommandsOperation;
import com.foros.rs.client.model.restriction.RestrictionParam;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;


public class RestrictionServiceTest extends AbstractUnitTest {

    private Long agencyId;

    @Before
    public void setUp() throws Exception {
        agencyId = longProperty("foros.test.agency.id");
    }

    @Test
    public void testSearch() throws Exception {
        RestrictionCommand restrictionCommand1 = new RestrictionCommand();
        restrictionCommand1.setName("AdopsDashboard.run");

        RestrictionCommand restrictionCommand2 = new RestrictionCommand();
        restrictionCommand2.setName("Context.switch");
        RestrictionParam param2 = new RestrictionParam();
        param2.setName("Advertiser");
        restrictionCommand2.setParams(Collections.singletonList(param2));

        RestrictionCommand restrictionCommand3 = new RestrictionCommand();
        restrictionCommand3.setName("Account.update");
        RestrictionParam param3 = new RestrictionParam();
        param3.setName("com.foros.model.account.Account");
        param3.setId(agencyId);
        restrictionCommand3.setParams(Collections.singletonList(param3));

        RestrictionCommandsOperation operation = new RestrictionCommandsOperation();
        operation.setRestrictionCommands(Arrays.asList(restrictionCommand1, restrictionCommand2, restrictionCommand3));

        Predicates result = restrictionService.get(operation);
        assertNotNull(result);
        assertEquals(result.getPredicates().size(), 3);
        assertTrue(result.getPredicates().get(0));
        assertTrue(result.getPredicates().get(1));
        assertTrue(result.getPredicates().get(2));
    }
}

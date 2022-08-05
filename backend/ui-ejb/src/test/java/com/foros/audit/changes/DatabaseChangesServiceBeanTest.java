package com.foros.audit.changes;

import com.foros.AbstractServiceBeanIntegrationTest;

import java.util.Collection;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseChangesServiceBeanTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private DatabaseChangesService databaseChangesService;

    @Test
    public void testReadChanges() throws Exception {
        Collection<ChangeRecord> changeRecords = databaseChangesService.readChanges();
        assertNotNull(changeRecords);
    }

    @Test
    public void testReadPersistentChanges() throws Exception {
        Collection<ChangeRecord> changeRecords = databaseChangesService.readPersistentChanges();
        assertNotNull(changeRecords);
    }
}
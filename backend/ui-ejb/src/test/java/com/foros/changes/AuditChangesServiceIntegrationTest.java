package com.foros.changes;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.audit.changes.DatabaseChangesServiceBean;

import group.Db;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class AuditChangesServiceIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private DatabaseChangesServiceBean changeService;

    @Test
    public void test() {
         assertNotNull(changeService);
    }
}

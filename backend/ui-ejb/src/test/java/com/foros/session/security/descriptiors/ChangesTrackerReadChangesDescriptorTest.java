package com.foros.session.security.descriptiors;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.audit.changes.ChangeRecord;
import com.foros.audit.changes.DatabaseChangesServiceBean;
import com.foros.util.PersistenceUtils;

import group.Db;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class ChangesTrackerReadChangesDescriptorTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private DatabaseChangesServiceBean changeService;

    @Test
    public void testChanges() throws Exception {
        Session session = PersistenceUtils.getHibernateSession(entityManager);

        Collection<ChangeRecord> result;

        // no changes in the beginning
        result = changeService.readChanges();
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // simulate update
        addChanges(session);

        // read changes (and auto-clear)
        result = changeService.readChanges();
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // no more changes to read
        result = changeService.readChanges();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void addChanges(Session session) {
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                CallableStatement st = connection.prepareCall("{call changestracker.add_bulk_change('CHANNEL', '{1}') }");
                st.execute();
            }
        });
    }
}

package com.foros.audit.changes;

import com.foros.session.LoggingJdbcTemplate;
import com.foros.util.PersistenceUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

@Singleton(name = "DatabaseChangesService")
public class DatabaseChangesServiceBean implements DatabaseChangesService {
    private static final Logger logger = Logger.getLogger(DatabaseChangesServiceBean.class.getName());

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    private Map<String, String> tableToClass;

    @PostConstruct
    public void init() {
        SessionFactory sf = PersistenceUtils.getHibernateSession(em).getSessionFactory();
        @SuppressWarnings("unchecked")
        Map<String, ClassMetadata> classesMetadata = sf.getAllClassMetadata();
        tableToClass = new HashMap<>();

        for (ClassMetadata metadata : classesMetadata.values()) {
            String entityName = ((EntityPersister)metadata).getRootEntityName();
            String tableName = ((Joinable)metadata).getTableName();
            tableToClass.put(tableName, entityName);
        }
    }

    @Override
    public Collection<ChangeRecord> readChanges() {
        Set<ChangeRecord> changes = jdbcTemplate.query("select * from changestracker.read_changes()", changesCallback());
        jdbcTemplate.execute("select changestracker.delete_changes()");
        return changes;
    }


    @Override
    public Collection<ChangeRecord> readPersistentChanges() {
        return jdbcTemplate.query("select * from changestracker.read_persistent_changes(true)", changesCallback());
    }

    private ChangeRecordRowCallbackHandler changesCallback() {
        return new ChangeRecordRowCallbackHandler();
    }

    private class ChangeRecordRowCallbackHandler implements ResultSetExtractor<Set<ChangeRecord>> {
        @Override
        public Set<ChangeRecord> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Set<ChangeRecord> changes = new HashSet<>();
            while (rs.next()) {
                String tableName = rs.getString("table_name");
                Long id = rs.getLong("id");
                String persistentClass = tableToClass.get(tableName.toUpperCase());
                if (persistentClass != null) {
                    changes.add(new ChangeRecord(id, persistentClass));
                } else {
                    logger.log(Level.WARNING, "Can't find persistent class for table: " + tableName);
                }
            }
            return changes;
        }
    }
}

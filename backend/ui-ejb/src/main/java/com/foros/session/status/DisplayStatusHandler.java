package com.foros.session.status;

import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.security.descriptiors.displaystatus.DisplayStatusEntry;
import com.foros.util.PersistenceUtils;

import java.util.HashSet;
import java.util.Set;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.persister.entity.AbstractEntityPersister;

public class DisplayStatusHandler {

    private Set<DisplayStatusEntry> entriesList = new HashSet<>();
    private LoggingJdbcTemplate jdbcTemplate;
    private Session session;

    public void initialize(LoggingJdbcTemplate jdbcTemplate, Session session) {
        if (this.jdbcTemplate == null) {
            this.jdbcTemplate = jdbcTemplate;
        }
        if (this.session == null) {
            this.session = session;
        }
    }

    public void registerEntity(Object entity, Long id) {
        Class<?> persistentClass = Hibernate.getClass(entity);
        AbstractEntityPersister persister = (AbstractEntityPersister) session.getSessionFactory().getClassMetadata(persistentClass);
        String tableName = persister.getTableName().toLowerCase();
        entriesList.add(new DisplayStatusEntry(id, tableName));
    }

    public void processDisplayStatuses() {
        if (session == null || jdbcTemplate == null || entriesList.isEmpty()) {
            return;
        }

        jdbcTemplate.execute(
                "select displaystatus.bulk_update_display_status(?)",
                jdbcTemplate.createArray("table_id", entriesList)
        );
        PersistenceUtils.scheduleEviction(session);
        clear();
    }

    private void clear() {
        entriesList.clear();
        this.session = null;
        this.jdbcTemplate = null;
    }
}

package com.foros.cache.generic;

import com.foros.util.PersistenceUtils;

import java.util.Collection;
import java.util.Collections;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;

public final class EntityIdTag {

    private String entity;
    private Long id;

    EntityIdTag(String entity, Long id) {
        this.entity = entity;
        this.id = id;
    }

    public Collection<EntityIdTag> asCollection() {
        return Collections.singletonList(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityIdTag that = (EntityIdTag) o;

        if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entity != null ? entity.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    public static EntityIdTag create(String name, Long id) {
        return new EntityIdTag(name, id);
    }

    public static EntityIdTag create(Session session, Class<?> type, Long id) {
        return new EntityIdTag(PersistenceUtils.getRootEntityName(session, type), id);
    }

    public static EntityIdTag create(EntityManager em, Class<?> type, Long id) {
        return create(PersistenceUtils.getHibernateSession(em), type, id);
    }

    public static EntityIdTag create(Session session, Object o) {
        SessionImplementor sessionImpl = (SessionImplementor) session;
        EntityPersister persister = sessionImpl.getEntityPersister(null, o);
        // non long ids is not supported now
        Long id = (Long) persister.getIdentifier(o, sessionImpl);
        String name = persister.getRootEntityName();
        return new EntityIdTag(name, id);
    }

}

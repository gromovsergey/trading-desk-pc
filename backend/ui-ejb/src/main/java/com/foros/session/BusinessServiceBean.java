package com.foros.session;

import com.foros.model.EntityBase;
import com.foros.util.EntityUtils;
import com.foros.util.StringUtil;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.commons.beanutils.PropertyUtils;

@SuppressWarnings({"EjbErrorInspection"})
public abstract class BusinessServiceBean<E extends EntityBase> {
    protected Class<E> entityClass;

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    protected Logger logger;

    public BusinessServiceBean(Class<E> entityClass) {
        this.entityClass = entityClass;
        this.logger = Logger.getLogger(BusinessServiceBean.class.getName());
    }

    public void create(E entity) {
        try {
            em.persist(entity);
            em.flush();
        } catch (RuntimeException ex) {
            String field = EntityUtils.findIdPropertyName(entityClass);
            try {
                if (StringUtil.isPropertyNotEmpty(field)) {
                    PropertyUtils.setProperty(entity, field, null);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Can't create entity", e);
            }
            throw ex;
        }
    }

    public E update(E entity) {
        E merged = em.merge(entity);
        em.flush();
        return merged;
    }

    @Deprecated
    public void refresh(Long id) {
        E e = em.find(entityClass, id);
        em.refresh(e);
    }

    public E findById(Long id) {
        E entity = null;
        if (id != null) {
             entity = em.find(entityClass, id);
        }

        if (entity == null) {
            throw new EntityNotFoundException(entityClass.getSimpleName() + " with id=" + id + " not found");
        }

        return entity;
    }

    public List<E> findAll() {
        String qName = entityClass.getSimpleName() + ".findAll";

        @SuppressWarnings("unchecked")
        List<E> result = em.createNamedQuery(qName).getResultList();

        return result;
    }
}

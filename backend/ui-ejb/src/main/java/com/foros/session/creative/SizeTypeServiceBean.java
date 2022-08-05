package com.foros.session.creative;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.creative.SizeType;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.security.AuditService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless(name = "SizeTypeService")
@Interceptors({RestrictionInterceptor.class, PersistenceExceptionInterceptor.class, ValidationInterceptor.class})
public class SizeTypeServiceBean implements SizeTypeService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @Override
    public SizeType findByName(String name) {
        try {
            return (SizeType) em.createQuery("select st from SizeType st where st.defaultName = :name")
                .setParameter("name", name)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Restrict(restriction = "CreativeSize.create")
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "SizeType.create", parameters = {"#sizeType"})
    public void create(SizeType sizeType) {
        auditService.audit(sizeType, ActionType.CREATE);
        em.persist(sizeType);
    }

    @Override
    @Restrict(restriction = "CreativeSize.update")
    @Validate(validation = "SizeType.update", parameters = {"#sizeType"})
    @Interceptors(CaptureChangesInterceptor.class)
    public void update(SizeType sizeType) {
        sizeType = em.merge(sizeType);
        auditService.audit(sizeType, ActionType.UPDATE);
    }

    @Override
    public SizeType find(Long id) {
        if (id == null) {
            throw new EntityNotFoundException("SizeType with id=" + id + " not found");
        }
        SizeType res = em.find(SizeType.class, id);
        if (res == null) {
            throw new EntityNotFoundException("SizeType with id=" + id + " not found");
        }
        return res;

    }

    @Override
    @Restrict(restriction = "CreativeSize.view")
    public SizeType view(Long id) {
        return find(id);
    }

    @Override
    public List<SizeTypeTO> findByAccountType(Long accountTypeId) {
        List list = em.createQuery("SELECT distinct NEW com.foros.session.creative.SizeTypeTO(st.id, st.defaultName, st.flags) "
                + "FROM AccountType at join at.creativeSizes cs join cs.sizeType st  "
                + " WHERE at.id = :accountTypeId and cs.status <> 'D'")
            .setParameter("accountTypeId", accountTypeId)
            .getResultList();
        Collections.sort(list);
        return list;
    }

    @Override
    @Restrict(restriction = "CreativeSize.view")
    public List<SizeType> findAll() {
        String sql = "from SizeType";
        //noinspection unchecked
        List<SizeType> list = em.createQuery(sql).getResultList();
        Collections.sort(list, new LocalizableNameEntityComparator());
        return list;
    }
}

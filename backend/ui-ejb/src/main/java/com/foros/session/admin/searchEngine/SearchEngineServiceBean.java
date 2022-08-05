package com.foros.session.admin.searchEngine;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.admin.SearchEngine;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.security.AuditService;
import com.foros.validation.ValidateBeanInterceptor;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.strategy.ValidationMode;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "SearchEngineService")
@Interceptors({PersistenceExceptionInterceptor.class, RestrictionInterceptor.class})
public class SearchEngineServiceBean implements SearchEngineService {

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    private AuditService auditService;

    @SuppressWarnings("unchecked")
    @Override
    @Restrict(restriction = "SearchEngine.view")
    public List<SearchEngine> list() {
        Query q = em.createNamedQuery("SearchEngine.list");
        return q.getResultList();
    }

    @Override
    @Interceptors({CaptureChangesInterceptor.class, ValidationInterceptor.class})
    @Restrict(restriction = "SearchEngine.create")
    @Validate(validation = "SearchEngine.create", parameters = "#searchEngine")
    public Long create(SearchEngine searchEngine) {
        auditService.audit(searchEngine, ActionType.CREATE);
        em.persist(searchEngine);
        return searchEngine.getId();
    }

    @Override
    @Interceptors({CaptureChangesInterceptor.class, ValidationInterceptor.class})
    @Restrict(restriction = "SearchEngine.update")
    @Validate(validation = "SearchEngine.update", parameters = "#searchEngine")
    public void update(SearchEngine searchEngine) {
        searchEngine = em.merge(searchEngine);
        auditService.audit(searchEngine, ActionType.UPDATE);

    }

    @Override
    @Restrict(restriction = "SearchEngine.update")
    public void delete(Long id) {
        em.remove(findById(id));
    }

    @Override
    public SearchEngine findById(Long id) {
        final SearchEngine searchEngine = em.find(SearchEngine.class, id);
        if (searchEngine == null) {
            throw new EntityNotFoundException("SearchEngine with id=" + id + " not found");
        }
        return searchEngine;
    }
}

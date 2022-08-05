package com.foros.session.admin.fraudConditions;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.admin.FraudCondition;
import com.foros.model.admin.FraudConditionWrapper;
import com.foros.model.admin.GlobalParam;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.admin.globalParams.GlobalParamsService;
import com.foros.session.security.AuditService;
import com.foros.util.CollectionMerger;
import com.foros.util.PersistenceUtils;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "FraudConditionsService")
@Interceptors({PersistenceExceptionInterceptor.class, RestrictionInterceptor.class, ValidationInterceptor.class})
public class FraudConditionsServiceBean implements FraudConditionsService {
    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    private GlobalParamsService globalParamsService;

    @EJB
    private AuditService auditService;

    @Override
    @Restrict(restriction = "FraudConditions.view")
    @SuppressWarnings("unchecked")
    public List<FraudCondition> findAll() {
        Query q = em.createNamedQuery("FraudCondition.findAll");
        return q.getResultList();
    }

    @Override
    @Restrict(restriction = "FraudConditions.update")
    @Validate(validation = "FraudCondition.update", parameters = { "#userInactivityTimeOut", "#fraudConditions" })
    @Interceptors({ CaptureChangesInterceptor.class })
    public void update(GlobalParam userInactivityTimeOut, List<FraudCondition> fraudConditions) {

        FraudConditionWrapper wrapper = new FraudConditionWrapper();
        GlobalParam globalParam = globalParamsService.updateUserInactivityTimeout(userInactivityTimeOut);
        wrapper.setUserInactivityTimeOut(globalParam);

        List<FraudCondition> existingFraudConditions = findAll();
        wrapper.setFraudConditions(existingFraudConditions);
        auditService.audit(wrapper, ActionType.UPDATE);

        final List<FraudCondition> toDelete = new ArrayList<FraudCondition>();
        final boolean[] isUpdated = new boolean[1];

        new CollectionMerger<FraudCondition>(existingFraudConditions, fraudConditions) {
            @Override
            protected boolean delete(FraudCondition fr) {
                toDelete.add(fr);
                em.remove(fr);
                isUpdated[0] = true;
                return true;
            }

            @Override
            protected void update(FraudCondition persistent, FraudCondition updated) {
                super.update(persistent, updated);
            }

            @Override
            protected boolean add(FraudCondition updated) {
                em.persist(updated);
                isUpdated[0] = true;
                return true;
            }
        }.merge();

        if (isUpdated[0]) {
            PersistenceUtils.performHibernateLock(em, globalParam);
        }
        
        wrapper.getFraudConditions().addAll(toDelete);

        em.flush();
    }

    @Override
    @Restrict(restriction = "FraudConditions.view")
    public GlobalParam getUserInactivityTimeout() {
        return globalParamsService.getUserInactivityTimeout();
    }
}

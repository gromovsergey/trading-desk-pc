package com.foros.session.admin;

import com.foros.model.ClobParam;
import com.foros.model.ClobParamPK;
import com.foros.model.ClobParamType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.util.EntityUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.interceptor.Interceptors;

@Stateless(name = "ClobParamService")
@Interceptors({RestrictionInterceptor.class, PersistenceExceptionInterceptor.class})
public class ClobParamServiceBean implements ClobParamService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    @Restrict(restriction = "Account.update", parameters = "find('InternalAccount', #param.id.accountId)")
    public void update(ClobParam param) {
        param.getChanges().remove("id");

        ClobParamPK id = param.getId();
        ClobParam existingParam = em.find(ClobParam.class, id);
        
        if (existingParam != null) {
            EntityUtils.copy(existingParam, param);
        } else {
            em.persist(param);
        }
    }

    @Override
    public ClobParam find(Long id, ClobParamType type) {
        return em.find(ClobParam.class, new ClobParamPK(id, type));
    }
}

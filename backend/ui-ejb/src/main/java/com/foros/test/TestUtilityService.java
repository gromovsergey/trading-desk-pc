package com.foros.test;

import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@LocalBean
public class TestUtilityService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;
    
    public <T> T getSingleResult(String hql, Map<String, Object> params) {
        List<T> list = query(hql, params).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    private Query query(String hql, Map<String, Object> params) {
        Query query = em.createQuery(hql);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }
}

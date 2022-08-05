package com.foros.session.admin;

import com.foros.cache.local.DynamicResourcesLocalCache;
import com.foros.cache.local.LocalizedResourcesLocalCache;
import com.foros.model.admin.DynamicResource;
import com.foros.model.admin.DynamicResourceId;
import com.foros.util.StringUtil;

import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "DynamicResourcesService")
public class DynamicResourcesServiceBean implements DynamicResourcesService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private DynamicResourcesLocalCache dynamicResourcesCache;

    @EJB
    private LocalizedResourcesLocalCache localizedResourcesCache;

    @Override
    public List<DynamicResource> findResources(String resourceKey) {
        if (StringUtil.isPropertyEmpty(resourceKey)) {
            return Collections.emptyList();
        }
        
        Query q = em.createNamedQuery("DynamicResource.findByKey");
        q.setParameter("key", resourceKey);

        return q.getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<DynamicResource> findLangResources(String lang) {
        if (StringUtil.isPropertyEmpty(lang)) {
            return Collections.emptyList();
        }

        Query q = em.createNamedQuery("DynamicResource.findByLang");
        q.setParameter("lang", lang);

        return q.getResultList();
    }

    @Override
    public void saveResources(List<DynamicResource> created, List<DynamicResource> updated, List<DynamicResource> deleted) {
        for (DynamicResource res : created) {
            em.merge(res);
        }

        for (DynamicResource res : updated) {
            em.merge(res);
        }

        for (DynamicResource res : deleted) {
            DynamicResource persistent = em.find(DynamicResource.class, new DynamicResourceId(res.getKey(), res.getLang()));
            if (persistent != null) {
                em.remove(persistent);
            }
        }

        refresh();
    }

    @Override
    public DynamicResource findResources(String resourceKey, String lang) {
        if (StringUtil.isPropertyEmpty(resourceKey) || StringUtil.isPropertyEmpty(lang))
            return new DynamicResource();

        return em.find(DynamicResource.class, new DynamicResourceId(resourceKey, lang));
    }

    @Override
    public void refresh() {
        dynamicResourcesCache.clear();
        localizedResourcesCache.clear();
     }
}

package com.foros.session.channel.service;

import com.foros.model.channel.Platform;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.util.PersistenceUtils;
import com.foros.util.expression.ExpressionHelper;
import com.foros.validation.ValidationInterceptor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "PlatformService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class PlatformServiceBean implements PlatformService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    public List<Platform> findAll() {
        return em.createQuery("select p from Platform p order by name", Platform.class).getResultList();
    }

    @Override
    public Collection<Platform> findByExpression(String expression) {
        Collection<Long> ids = ExpressionHelper.extractChannels(expression);
        if (ids.size() == 0) {
            return Collections.emptyList();
        }
        return em.createQuery("select p from Platform p where id in (:ids)", Platform.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public Platform findByName(String platformName) {
        try {
            Query query = em.createQuery("select p from Platform p where name = :platformName");
            query.setParameter("platformName", platformName);
            return (Platform) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Restrict(restriction = "DeviceChannel.get")
    @Override
    public Result<Platform> get(Paging paging) {
        List<Platform> res = em.createQuery("select p from Platform p order by name", Platform.class)
                .setFirstResult(paging.getFirst())
                .setMaxResults(paging.getCount())
                .getResultList();
        for (Platform platform : res) {
            PersistenceUtils.initializeCollection(platform.getPlatformDetectors());
        }
        return new Result<>(res, paging);
    }

    @Override
    public Platform findById(Long id) {
        Platform entity = em.find(Platform.class, id);
        if (entity == null) {
            throw new EntityNotFoundException("Platform with id=" + id + " not found");
        }
        entity.setPlatformDetectors(entity.getPlatformDetectors());
        return entity;
    }
}

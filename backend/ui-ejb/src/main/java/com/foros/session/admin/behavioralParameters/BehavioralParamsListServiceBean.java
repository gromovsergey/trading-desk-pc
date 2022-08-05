package com.foros.session.admin.behavioralParameters;

import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.Channel;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang.ObjectUtils;

@Stateless(name = "BehavioralParamsListService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class BehavioralParamsListServiceBean implements BehavioralParamsListService {
    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @Override
    @Restrict(restriction = "BehavioralParams.create")
    @Validate(validation = "BehavioralParamsList.create", parameters = "#params")
    public Long create(BehavioralParametersList params) {
        em.persist(params);
        em.flush();
        return params.getId();
    }

    @Override
    @Restrict(restriction = "BehavioralParams.update")
    @Validate(validation = "BehavioralParamsList.update", parameters = "#params")
    public void update(BehavioralParametersList params) {
        BehavioralParametersList existingParams = find(params.getId());
        List<BehavioralParameters> oldParams = new LinkedList<BehavioralParameters>(existingParams.getBehavioralParameters());

        removeParameters(oldParams, params.getBehavioralParameters());

        params = em.merge(params);
        em.flush();
    }

    private Collection<BehavioralParameters> calcSrcDiff(List<BehavioralParameters> srcParams, List<BehavioralParameters> tarParams) {
        List<BehavioralParameters> diff = new ArrayList<BehavioralParameters>();
        for (BehavioralParameters bp : srcParams) {
            if (!containsParameter(tarParams, bp)) {
                diff.add(bp);
            }
        }

        return diff;
    }

    private void removeParameters(List<BehavioralParameters> oldParams, List<BehavioralParameters> newParams) {
        Collection<BehavioralParameters> removed = calcSrcDiff(oldParams, newParams);
        if (!removed.isEmpty()) {
            for (BehavioralParameters bp : removed) {
                em.remove(bp);
            }
        }
    }

    private boolean containsParameter(List<BehavioralParameters> bps, BehavioralParameters existingParameter) {
        for (BehavioralParameters bp : bps) {
            if (ObjectUtils.equals(existingParameter.getId(), bp.getId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Restrict(restriction = "BehavioralParams.view")
    @SuppressWarnings("unchecked")
    public List<BehavioralParametersList> findAll() {
        Query q = em.createNamedQuery("BehavioralParametersList.findAll");
        return q.getResultList();
    }

    @Override
    public BehavioralParametersList find(Long id) {
        BehavioralParametersList bparamsList = doFind(id);
        bparamsList.getBehavioralParameters().size();
        return bparamsList;
    }

    @Override
    public BehavioralParametersList findWithNoErrors(Long id) {
        BehavioralParametersList bparamsList = em.find(BehavioralParametersList.class, id);

        if (bparamsList == null) {
            return null;
        }

        bparamsList.getBehavioralParameters().size();

        return bparamsList;
    }

    @Override
    @Restrict(restriction = "BehavioralParams.view")
    public BehavioralParametersList view(Long id) {
        return find(id);
    }


    private BehavioralParametersList doFind(Long id) {

        BehavioralParametersList bparamsList = em.find(BehavioralParametersList.class, id);
        if (bparamsList == null) {
            throw new EntityNotFoundException(BehavioralParametersList.class.getSimpleName() +
                    " with id=" + id + " not found");
        }

        return bparamsList;
    }

    @Override
    @Restrict(restriction = "BehavioralParams.delete", parameters = "find('BehavioralParametersList', #id)")
    @Validate(validation = "BehavioralParamsList.delete", parameters = "#id")
    public void delete(Long id) {
        em.remove(doFind(id));
    }

    @Override
    public int getChannelUsageCount(Long id) {
        Query q = em.createNativeQuery("select count(*) from channel c where c.channel_type in ('" + Channel.CHANNEL_TYPE_DISCOVER+"', " +
                " '" + Channel.CHANNEL_TYPE_DISCOVER_CHANNEL_LIST + "') " +
                " and c.behav_params_list_id = :id");
        q.setParameter("id", id);
        return ((Number) q.getSingleResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    private List<Number> findChannelUsage(Long id) {
        Query q = em.createNativeQuery("select channel_id from channel where channel_type = 'D' and behav_params_list_id = :id");
        q.setParameter("id", id);
        return q.getResultList();
    }
}

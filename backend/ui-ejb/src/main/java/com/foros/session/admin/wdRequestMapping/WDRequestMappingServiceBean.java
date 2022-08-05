package com.foros.session.admin.wdRequestMapping;

import com.foros.cache.NamedCO;
import com.foros.model.admin.GlobalParam;
import com.foros.model.admin.WDRequestMapping;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessServiceBean;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.admin.globalParams.GlobalParamsService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@Stateless(name = "WDRequestMappingService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class WDRequestMappingServiceBean extends BusinessServiceBean<WDRequestMapping> implements WDRequestMappingService {

    public WDRequestMappingServiceBean() {
        super(WDRequestMapping.class);
    }

    @Override
    @Restrict(restriction = "WDRequestMapping.view")
    public WDRequestMapping view(Long id) {
        return super.findById(id);
    }

    @Override
    @Restrict(restriction  = "WDRequestMapping.create")
    @Validate(validation = "WDRequestMapping.create", parameters = "#entity")
    public void create(WDRequestMapping entity) {
        super.create(entity);
    }

    @Override
    @Restrict(restriction = "WDRequestMapping.update")
    @Validate(validation = "WDRequestMapping.update", parameters = "#entity")
    public WDRequestMapping update(WDRequestMapping entity) {
        return super.update(entity);
    }

    @Override
    @Restrict(restriction = "WDRequestMapping.view")
    public List<WDRequestMapping> findAll() {
        return super.findAll();
    }

    @Override
    @Restrict(restriction = "WDRequestMapping.update")
    public void delete(Long id) {
        GlobalParam wdTagMapping = em.find(GlobalParam.class, GlobalParamsService.WD_TAG_MAPPING_ID);

        if (wdTagMapping != null) {
            String value = wdTagMapping.getValue();

            if (value != null && Long.valueOf(value).equals(id)) {
                wdTagMapping.setValue(null);
            }
        }

        em.remove(findById(id));
    }

    @Override
    public List<NamedCO<Long>> getIndex() {
        return em.createQuery("select new com.foros.cache.NamedCO(w.id,w.name) from WDRequestMapping w order by upper(w.name)").getResultList();
    }
}

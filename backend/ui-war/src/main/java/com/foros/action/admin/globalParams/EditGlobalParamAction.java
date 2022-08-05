package com.foros.action.admin.globalParams;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.cache.NamedCO;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.admin.GlobalParam;
import com.foros.session.admin.globalParams.GlobalParamsService;
import com.foros.session.admin.wdRequestMapping.WDRequestMappingService;

import java.util.List;

import javax.ejb.EJB;

public class EditGlobalParamAction extends GlobalParamActionSupport implements BreadcrumbsSupport {

    @EJB
    private GlobalParamsService service;

    @EJB
    private WDRequestMappingService wdRequestMappingService;

    private List<NamedCO<Long>> wdTagMappings;

    @ReadOnly
    public String edit() {
        GlobalParam exchangeRateUpdate = service.view(GlobalParamsService.CURRENCY_EXCHANGE_RATE_UPDATE);
        GlobalParam wdTagMapping = service.view(GlobalParamsService.WD_TAG_MAPPING_ID);
        getModel().setExchangeRateUpdate(exchangeRateUpdate);
        getModel().setWdTagMapping(wdTagMapping);
        return SUCCESS;
    }

    public List<NamedCO<Long>> getWdTagMappings() {
        if (wdTagMappings == null) {
            wdTagMappings = wdRequestMappingService.getIndex();
        }

        return wdTagMappings;
    }

    public GlobalParam getWdTagMapping() {
        return getModel().getWdTagMapping();
    }

    public GlobalParam getExchangeRateUpdate() {
        return getModel().getExchangeRateUpdate();
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new GlobalParamsBreadcrumbsElement()).add(ActionBreadcrumbs.EDIT);
    }
}

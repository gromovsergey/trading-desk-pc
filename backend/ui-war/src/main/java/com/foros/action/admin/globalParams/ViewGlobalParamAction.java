package com.foros.action.admin.globalParams;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.admin.GlobalParam;
import com.foros.model.admin.WDRequestMapping;
import com.foros.model.currency.Source;
import com.foros.session.admin.globalParams.GlobalParamsService;
import com.foros.session.admin.wdRequestMapping.WDRequestMappingService;
import com.foros.util.StringUtil;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class ViewGlobalParamAction extends BaseActionSupport implements ModelDriven<WDRequestMapping> {

    @EJB
    private GlobalParamsService paramsService;

    @EJB
    private WDRequestMappingService wdRequestMappingService;

    private Source exchangeRateSource;
    private WDRequestMapping wdTagMapping;

    @ReadOnly
    public String view() {
        GlobalParam rateUpdate = paramsService.view(GlobalParamsService.CURRENCY_EXCHANGE_RATE_UPDATE);
        exchangeRateSource = Source.valueOf(rateUpdate.getValue());
        GlobalParam wdTagMappingParam = paramsService.view(GlobalParamsService.WD_TAG_MAPPING_ID);
        wdTagMapping = wdTagMappingParam == null || StringUtil.isPropertyEmpty(wdTagMappingParam.getValue()) ? null :
                wdRequestMappingService.findById(Long.valueOf(wdTagMappingParam.getValue()));
        
        return SUCCESS;
    }

    @Override
    public WDRequestMapping getModel() {
        return wdTagMapping;
    }

    public Source getExchangeRateSource() {
        return exchangeRateSource;
    }
}

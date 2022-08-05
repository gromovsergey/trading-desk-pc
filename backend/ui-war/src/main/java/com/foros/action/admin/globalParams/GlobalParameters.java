package com.foros.action.admin.globalParams;

import com.foros.model.admin.GlobalParam;
import com.foros.session.admin.globalParams.GlobalParamsService;

public class GlobalParameters {
    private GlobalParam wdTagMapping = new GlobalParam(GlobalParamsService.WD_TAG_MAPPING_ID, null);

    private GlobalParam exchangeRateUpdate = new GlobalParam(GlobalParamsService.CURRENCY_EXCHANGE_RATE_UPDATE, null);

    public GlobalParam getWdTagMapping() {
        return wdTagMapping;
    }

    public void setWdTagMapping(GlobalParam wdTagMapping) {
        this.wdTagMapping = wdTagMapping;
    }

    public GlobalParam getExchangeRateUpdate() {
        return exchangeRateUpdate;
    }

    public void setExchangeRateUpdate(GlobalParam exchangeRateUpdate) {
        this.exchangeRateUpdate = exchangeRateUpdate;
    }
}

package com.foros.action.support;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.account.yandex.advertiser.YandexTnsAdvertiserService;
import com.foros.session.account.yandex.brand.YandexTnsBrandService;

import javax.ejb.EJB;

public class YandexJobsExecutorAction extends BaseActionSupport {
    @EJB
    private YandexTnsBrandService brandService;

    @EJB
    private YandexTnsAdvertiserService advertiserService;

    @ReadOnly
    public String main() throws Exception {
        return SUCCESS;
    }

    public String brandsSynchronize() {
        brandService.synchronize();
        return SUCCESS;
    }

    public String advertisersSynchronize() {
        advertiserService.synchronize();
        return SUCCESS;
    }
}

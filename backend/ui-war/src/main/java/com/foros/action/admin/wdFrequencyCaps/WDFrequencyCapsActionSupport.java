package com.foros.action.admin.wdFrequencyCaps;

import com.foros.action.BaseActionSupport;
import com.foros.model.FrequencyCap;
import com.foros.session.admin.globalParams.GlobalParamsService;

import javax.ejb.EJB;

public class WDFrequencyCapsActionSupport extends BaseActionSupport {
    @EJB
    protected GlobalParamsService globalParamsService;

    private FrequencyCap eventsFrequencyCap;

    private FrequencyCap channelsFrequencyCap;

    private FrequencyCap categoriesFrequencyCap;

    public FrequencyCap getEventsFrequencyCap() {
        return eventsFrequencyCap;
    }

    public void setEventsFrequencyCap(FrequencyCap eventsFrequencyCap) {
        this.eventsFrequencyCap = eventsFrequencyCap;
    }

    public FrequencyCap getChannelsFrequencyCap() {
        return channelsFrequencyCap;
    }

    public void setChannelsFrequencyCap(FrequencyCap channelsFrequencyCap) {
        this.channelsFrequencyCap = channelsFrequencyCap;
    }

    public FrequencyCap getCategoriesFrequencyCap() {
        return categoriesFrequencyCap;
    }

    public void setCategoriesFrequencyCap(FrequencyCap categoriesFrequencyCap) {
        this.categoriesFrequencyCap = categoriesFrequencyCap;
    }
}

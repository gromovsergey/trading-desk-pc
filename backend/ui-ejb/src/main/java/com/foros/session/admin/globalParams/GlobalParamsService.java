package com.foros.session.admin.globalParams;

import com.foros.model.FrequencyCap;
import com.foros.model.admin.GlobalParam;

import javax.ejb.Local;

@Local
public interface GlobalParamsService {
    String WD_FREQ_CAP_EVENT = "WD.FREQ_CAP_EVENT";
    
    String WD_FREQ_CAP_CHANNEL = "WD.FREQ_CAP_CHANNEL";
    
    String WD_FREQ_CAP_CATEGORY = "WD.FREQ_CAP_CATEGORY";

    String WD_TAG_MAPPING_ID = "WD.TAG_MAPPING_ID";

    String USER_INACTIVITY_TIMEOUT = "USER_INACTIVITY_TIMEOUT";

    String KEYWORD_CHANNEL_SEARCH_BEHAV_PARAMS_ID = "KEYWORD_CHANNEL.SEARCH.BEHAV_PARAMS_ID";

    String KEYWORD_CHANNEL_PAGE_BEHAV_PARAMS_ID = "KEYWORD_CHANNEL.PAGE.BEHAV_PARAMS_ID";

    String CURRENCY_EXCHANGE_RATE_UPDATE = "CURRENCY.EXCHANG_RATE_UPDATE";

    GlobalParam find(String name);

    GlobalParam findByValue(String value);

    GlobalParam view(String name);

    void updateExchangeRateUpdate(GlobalParam exchangeRateUpdate);

    void updateWDTagMapping(GlobalParam wdTagMapping);

    FrequencyCap getWDFrequencyCap(String name);

    void updateWDFrequencyCaps(FrequencyCap eventsFrequencyCap, FrequencyCap categoriesFrequencyCap, FrequencyCap channelsFrequencyCap);

    GlobalParam getUserInactivityTimeout();

    GlobalParam updateUserInactivityTimeout(GlobalParam userInactivityTimeOut);
}

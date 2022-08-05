package com.foros.util;

import static com.foros.config.ConfigParameters.CREATIVES_PATH;
import static com.foros.config.ConfigParameters.DATA_URL;
import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.PublisherAccount;

public class TemplateUtil {
    private static final String INTERACTIVE_CLICK_SERVICE_PATH = "/clickTracking*url=";

    public static String getImagePath(Config config, AdvertiserAccount advertiserAccount) {
        return getCreativesURL(config) + getImagePathRelative(advertiserAccount);
    }

    private static String getImagePathRelative(AdvertiserAccount advertiserAccount) {
        return (advertiserAccount.isInAgencyAdvertiser() ? ("/" + advertiserAccount.getAgency().getId() + "/" + advertiserAccount.getId() + "/")
                                                                                   : ("/" + advertiserAccount.getId() + "/"));
    }

    public static String getImagePath(Config config, PublisherAccount account) {
        return getDataUrl(config) + "/" + config.get(ConfigParameters.PUBL_PATH) + "/" + account.getId() + "/";
    }

    public static String getDataUrl(Config config) {
        return UrlUtil.stripUrl(config.get(DATA_URL));
    }

    public static String getInteractiveClickServiceUrl(Config config) {
        return UrlUtil.stripUrl(config.get(DATA_URL)) + INTERACTIVE_CLICK_SERVICE_PATH;
    }

    public static String getCreativesURL(Config config) {
        return getDataUrl(config) + "/" + config.get(CREATIVES_PATH);
    }

}

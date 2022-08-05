package com.foros.action.site;

import com.foros.action.BaseActionSupport;
import com.foros.model.site.Site;

public class SiteCreativesApprovalActionSupport extends BaseActionSupport {
    private Site site = new Site();

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}

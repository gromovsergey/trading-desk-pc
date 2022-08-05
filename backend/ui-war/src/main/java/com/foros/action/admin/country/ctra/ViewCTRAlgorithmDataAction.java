package com.foros.action.admin.country.ctra;

import com.foros.action.admin.country.CountriesBreadcrumbsElement;
import com.foros.action.admin.country.CountryBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.ctra.CTRAlgorithmAdvertiserExclusion;
import com.foros.model.ctra.CTRAlgorithmCampaignExclusion;
import com.foros.util.StringUtil;

public class ViewCTRAlgorithmDataAction extends CTRAlgorithmActionSupport implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    public String edit() {
        String res = process();
        breadcrumbs = new Breadcrumbs().add(new CountriesBreadcrumbsElement()).add(new CountryBreadcrumbsElement(data.getCountry())).add(new CTRAlgorithmBreadcrumbsElement(data.getCountry())).add(ActionBreadcrumbs.EDIT);
        return res;
    }

    @ReadOnly
    public String view() {
        String res = process();
        breadcrumbs = new Breadcrumbs().add(new CountriesBreadcrumbsElement()).add(new CountryBreadcrumbsElement(data.getCountry())).add(new CTRAlgorithmTextBreadcrumbsElement());
        return res;
    }

    public String process() {
        String id = getId();
        if (StringUtil.isNumber(id)) {
            data = ctrAlgorithmService.findByCountryId(Long.valueOf(id));
        } else {
            data = ctrAlgorithmService.find(id);
        }
        convertAdvertiserExclusions();
        formatCampaignExclusions();
        return SUCCESS;
    }

    private void convertAdvertiserExclusions() {
        for (CTRAlgorithmAdvertiserExclusion exclusion : data.getAdvertiserExclusions()) {
            advertiserExclusionsIds.add(exclusion.getPk().getAdvertiserId());
        }
    }

    private void formatCampaignExclusions() {
        StringBuilder textBuilder = new StringBuilder();
        int i = 0;
        int lastIdx = data.getCampaignExclusions().size() - 1;
        for (CTRAlgorithmCampaignExclusion exclusion : data.getCampaignExclusions()) {
            textBuilder.append(exclusion.getPk().getCampaignId());
            if (i++ < lastIdx) {
                textBuilder.append(", ");
            }
        }
        textBuilder.trimToSize();
        campaignExclusionsText = textBuilder.toString();
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}

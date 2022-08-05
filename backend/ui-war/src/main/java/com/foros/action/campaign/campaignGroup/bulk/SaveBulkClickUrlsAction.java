package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;

public class SaveBulkClickUrlsAction extends BulkClickUrlsActionSupport {
    @EJB
    private CampaignCreativeService creativeLinkService;

    @EJB
    private DisplayCreativeService displayCreativeService;

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("clickUrl", "violation.message")
            .add("clickUrl(#path)", "violation.message")
            .rules();

    private String url;
    private String append;
    private String search;
    private String replace;

    private void setClickUrls() {
        if (StringUtils.isBlank(url)) {
            return;
        }
        displayCreativeService.setClickUrl(getCreativeIds(), url);
    }

    private void appendClickUrls() {
        if (StringUtils.isBlank(append)) {
            return;
        }

        displayCreativeService.appendClickUrl(getCreativeIds(), append);
    }

    private void findReplaceClickUrls() {
        if (StringUtils.isBlank(search)) {
            return;
        }

        displayCreativeService.findReplaceClickUrl(getCreativeIds(), search, replace);
    }

    private List<Long> getCreativeIds() {
        return creativeLinkService.findCreativeIdsForBulkUpdate(campaignId, ids);
    }

    public String getAppend() {
        return append;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    @Override
    public List<Long> getIds() {
        return ids;
    }

    public String getReplace() {
        return replace;
    }

    public String getSearch() {
        return search;
    }

    public String getUrl() {
        return url;
    }

    public String save() {
        switch (editMode) {
            case Set:
                setClickUrls();
                break;
            case Append:
                appendClickUrls();
                break;
            case Replace:
                findReplaceClickUrls();
                break;
        }
        return SUCCESS;
    }

    public void setAppend(String append) {
        this.append = append;
    }


    @Override
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public void setReplace(String replace) {
        this.replace = replace;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

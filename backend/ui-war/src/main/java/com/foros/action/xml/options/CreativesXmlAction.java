package com.foros.action.xml.options;

import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.util.EntityUtils;
import com.foros.util.PairUtil;
import com.foros.util.StringUtil;

import java.util.Collection;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class CreativesXmlAction extends AbstractOptionsByAccountAction<EntityTO> {
    @EJB
    private DisplayCreativeService displayCreativeService;

    private String accountPair;
    private String campaignPair;
    private String creativeGroupPair;

    public CreativesXmlAction() {
        super(new NamedTOConverter(true), new OptionStatusFilter(true));
    }

    @AccountId
    @RequiredStringValidator(key = "errors.required", message = "value.accountPair")
    @CustomValidator(type = "pair", key = "errors.pair", message = "value.accountPair")
    public String getAccountPair() {
        return accountPair;
    }

    public void setAccountPair(String accountPair) {
        this.accountPair = accountPair;
    }

    public String getCampaignPair() {
        return campaignPair;
    }

    public void setCampaignPair(String campaignPair) {
        this.campaignPair = campaignPair;
    }

    public String getCreativeGroupPair() {
        return creativeGroupPair;
    }

    public void setCreativeGroupPair(String creativeGroupPair) {
        this.creativeGroupPair = creativeGroupPair;
    }

    public DisplayCreativeService getCreativeService() {
        return displayCreativeService;
    }

    protected Long fetchId(String pair) {
        return StringUtil.isPropertyEmpty(pair) ? null : PairUtil.fetchId(pair);
    }

    @Override
    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        Long campaignId = fetchId(getCampaignPair());
        Long creativeGroupId = fetchId(getCreativeGroupPair());

        if (creativeGroupId != null) {
            return EntityUtils.sortByStatus(getCreativeService().findByCreativeGroupId(creativeGroupId));
        }

        if (campaignId != null) {
            return EntityUtils.sortByStatus(getCreativeService().findByCampaignId(campaignId));
        }

        return EntityUtils.sortByStatus(getCreativeService().findEntityTOByAdvertiser(accountId));
    }
}

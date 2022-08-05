package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.action.ActionService;
import com.foros.util.PairUtil;
import com.foros.util.StringUtil;

import java.util.Collection;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;

public class ActionsXmlAction extends AbstractOptionsAction<EntityTO> {
    @EJB
    private ActionService actionService;

    @EJB
    private CurrentUserService currentUserService;

    private String groupPair;

    private String accountPair;

    private String advertiserPair;

    private String campaignPair;

    public ActionsXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(true));
    }

    @CustomValidator(type = "pair", key = "errors.pair", message = "campaignPair")
    public String getGroupPair() {
        return groupPair;
    }

    public void setGroupPair(String groupPair) {
        this.groupPair = groupPair;
    }

    @CustomValidator(type = "pair", key = "errors.pair", message = "accountPair")
    public String getAccountPair() {
        return accountPair;
    }

    public void setAccountPair(String accountPair) {
        this.accountPair = accountPair;
    }

    @CustomValidator(type = "pair", key = "errors.pair", message = "advertiserPair")
    public String getAdvertiserPair() {
        return advertiserPair;
    }

    public void setAdvertiserPair(String advertiserPair) {
        this.advertiserPair = advertiserPair;
    }

    @CustomValidator(type = "pair", key = "errors.pair", message = "campaignPair")
    public String getCampaignPair() {
        return campaignPair;
    }

    public void setCampaignPair(String campaignPair) {
        this.campaignPair = campaignPair;
    }

    public ActionService getActionService() {
        return actionService;
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        Long groupId = fetchId(getGroupPair());
        Long campaignId = fetchId(getCampaignPair());
        Long agencyId = fetchId(getAccountPair());
        Long advertiserId = fetchId(getAdvertiserPair());
        return actionService.findEntityTOByMultipleParameters(advertiserId == null ? agencyId : advertiserId, campaignId, groupId, currentUserService.getUser().isDeletedObjectsVisible());
    }

    private Long fetchId(String pair) {
        return StringUtil.isPropertyEmpty(pair) ? null : PairUtil.fetchId(pair);
    }

    @Override
    public void validate() {
        super.validate();
    }

}

package com.foros.action.action;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.account.AccountService;
import com.foros.session.action.ActionService;
import com.foros.session.action.ActionTO;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.session.security.UserService;
import com.foros.util.context.RequestContexts;

import java.util.List;
import javax.ejb.EJB;

public class ListActionAction extends BaseActionSupport implements RequestContextsAware, AdvertiserSelfIdAware {

    @EJB
    private ActionService actionService;

    @EJB
    private AccountService accountService;

    @EJB
    private UserService userService;

    // parameter
    private Long advertiserId;
    private String fastChangeId = "TOT";
    private DateRange dateRange = new DateRange();

    // model
    private List<ActionTO> actions;

    @ReadOnly
    public String list() {
        AdvertiserAccount advertiser = accountService.findAdvertiserAccount(advertiserId);
        if ("TOT".equals(fastChangeId)) {
            dateRange.setBegin(null);
            dateRange.setEnd(null);
        }
        actions = actionService.findByAccountIdAndDate(advertiser.getId(), dateRange.getBegin(), dateRange.getEnd(), userService.getMyUser().isDeletedObjectsVisible());
        return SUCCESS;
    }

    @Override
    public void switchContext(RequestContexts context) {
        context.getAdvertiserContext().switchTo(advertiserId);
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public List<ActionTO> getActions() {
        return actions;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public String getFastChangeId() {
        return fastChangeId;
    }

    public void setFastChangeId(String fastChangeId) {
        this.fastChangeId = fastChangeId;
    }
}

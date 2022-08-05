package com.foros.session.security;

import com.foros.session.account.AccountRestrictions;
import static com.foros.security.AccountRole.ADVERTISER;
import static com.foros.security.AccountRole.CMP;
import static com.foros.security.AccountRole.ISP;
import static com.foros.security.AccountRole.PUBLISHER;

import com.foros.model.account.Account;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.session.CurrentUserService;
import com.foros.security.AccountRole;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.colocation.ColocationRestrictions;
import com.foros.session.restriction.EntityRestrictions;
import com.foros.session.site.PublisherEntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class ContextRestrictions {
    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private EntityRestrictions entityRestrictions;
    
    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private ColocationRestrictions colocationRestrictions;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private PublisherEntityRestrictions publisherEntityRestrictions;

    @Restriction
    public boolean canSwitch(AccountRole role) {
        switch (role) {
            case ADVERTISER:
            case AGENCY:
                return canSwitchToAdvertiser();
            case PUBLISHER:
                return canSwitchToPublisher();
            case ISP:
                return canSwitchToISP();
            case CMP:
                return canSwitchToCMP();
            default:
                return false;
        }
    }

    @Restriction
    public boolean canSwitch(String roleName) {
        return canSwitch(AccountRole.byName(roleName));
    }

    @Restriction
    public boolean canSwitch(Account account) {
        return entityRestrictions.canView(account)
                && (currentUserService.isExternal() || canSwitch(account.getRole()));
    }

    private boolean canSwitchToAdvertiser() {
        return accountRestrictions.canView(ADVERTISER)
                || advertisingChannelRestrictions.canView(ADVERTISER)
                || advertiserEntityRestrictions.canView();
    }

    private boolean canSwitchToPublisher() {
        return accountRestrictions.canView(PUBLISHER)
                || publisherEntityRestrictions.canView();
    }

    private boolean canSwitchToISP() {
        return accountRestrictions.canView(ISP)
                || colocationRestrictions.canView();
    }

    private boolean canSwitchToCMP() {
        return accountRestrictions.canView(CMP)
                || advertisingChannelRestrictions.canView(CMP);
    }
}

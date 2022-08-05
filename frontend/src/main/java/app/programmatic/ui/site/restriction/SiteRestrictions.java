package app.programmatic.ui.site.restriction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.programmatic.ui.account.dao.model.PublisherAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.model.OwnedStatusable;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;
import app.programmatic.ui.common.restriction.service.EntityRestrictions;
import app.programmatic.ui.site.service.SiteService;

@Service
@Restrictions
public class SiteRestrictions {

    @Autowired
    private EntityRestrictions entityRestrictions;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SiteService siteService;

    @Restriction("site.viewSiteList")
    public boolean canViewSiteList(Long accountId) {
        PublisherAccount account = accountService.findPublisherUnchecked(accountId);
        return entityRestrictions.canViewEdit(account);
    }

    @Restriction("site.viewTags")
    public boolean canViewTags(Long siteId) {
        OwnedStatusable site = siteService.findSiteUnchecked(siteId);
        return entityRestrictions.canViewEdit(site);
    }
}

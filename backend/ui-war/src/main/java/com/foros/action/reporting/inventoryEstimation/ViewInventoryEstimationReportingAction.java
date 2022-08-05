package com.foros.action.reporting.inventoryEstimation;

import com.foros.action.BaseActionSupport;
import com.foros.util.helper.IndexHelper;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.PublisherSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.site.Tag;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.security.AccountTO;
import com.foros.session.site.TagsService;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;

public class ViewInventoryEstimationReportingAction extends BaseActionSupport implements RequestContextsAware, PublisherSelfIdAware {

    private Long accountId;
    private Long siteId;
    private Long tagId;

    private List<AccountTO> accounts;

    private List<EntityTO> sites;

    private List<EntityTO> tags;

    @EJB
    private AccountService accountService;

    @EJB
    private TagsService tagsService;

    @EJB
    private CurrentUserService currentUserService;

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'inventoryEstimation'")
    public String view() throws Exception {
        if (tagId != null) {
            Tag tag = tagsService.view(tagId);
            siteId = tag.getSite().getId();
            accountId = tag.getAccount().getId();
        }
        return "success";
    }

    public List<AccountTO> getAccounts() {
        if (accounts == null) {
            accounts = IndexHelper.getInventoryPublisherAccountList();
            if (accounts.size() > 0) {
                accountId = accounts.get(0).getId();
            }
        }
        return accounts;
    }

    public List<EntityTO> getSites() {
        if (sites == null) {
            List<EntityTO> sites = IndexHelper.getSitesList(getAccountId());
            this.sites = new ArrayList<EntityTO>(EntityUtils.applyStatusRules(sites, null, isInternal()));

        }
        return sites;
    }

    public List<EntityTO> getTags() {
        if (tags == null && siteId != null) {
            Collection<EntityTO> tags = IndexHelper.getTagsList(siteId);
            this.tags = new ArrayList<EntityTO>(EntityUtils.applyStatusRules(tags, null, isInternal()));
        }
        return tags;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if (accountId != null) {
            Account account = accountService.find(accountId);
            contexts.switchTo(account);
        }
    }

    public boolean isInternal() {
        return currentUserService.isInternal();
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public void setPublisherId(Long publisherId) {
        accountId = publisherId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getSiteId() {
        return this.siteId;
    }
}
package com.foros.action.reporting.referrer;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.PublisherSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.site.SiteService;
import com.foros.session.site.TagsService;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;
import com.foros.util.helper.IndexHelper;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.EJB;

public class ViewReferrerReportingAction extends BaseActionSupport implements RequestContextsAware, PublisherSelfIdAware {

    @EJB
    private SiteService siteService;

    @EJB
    private TagsService tagsService;

    @EJB
    private AccountService accountService;

    private Collection<EntityTO> sites;

    private Collection<EntityTO> tags;

    private Long siteId;

    private Long tagId;

    private Tag tag;

    private Site site;

    private Long accountId;

    private Boolean useImpala = true;

    @ReadOnly
    @Restrict(restriction = "Report.ReferrerReport.run", parameters = "#target.accountId")
    public String view() throws Exception {
        init();
        return "success";
        }

    private void init() {
        if (tagId != null) {
            tag = tagsService.view(tagId);
            siteId = tag.getSite().getId();
        }
        if (siteId != null) {
            site = siteService.view(siteId);
            accountId = site.getAccount().getId();
        }
    }

    public Collection<EntityTO> getSites() {
        if (sites == null) {
            sites = IndexHelper.getSitesList(accountId);
            EntityUtils.applyStatusRules(sites, null, true);
        }
        return sites;
    }

    public Collection<EntityTO> getTags() {
        if (tags == null) {
            Long tagSiteId = null;
            if (siteId != null) {
                tagSiteId = siteId;
            } else {
                getSites();
                if (sites != null && !sites.isEmpty()) {
                    tagSiteId = sites.iterator().next().getId();
                }
            }
            if (tagSiteId != null) {
                tags = IndexHelper.getTagsList(tagSiteId);
                EntityUtils.applyStatusRules(tags, null, false);
            } else {
                tags = Collections.EMPTY_LIST;
            }
        }
        return tags;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getSiteName() {
        return site.getName();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getTagName() {
        return tag.getName();
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getAccountId() {
        if (accountId == null) {
            init();
        }
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Boolean getUseImpala() {
        return useImpala;
    }

    public void setUseImpala(Boolean useImpala) {
        this.useImpala = useImpala;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        Account account = accountService.find(accountId);
        contexts.switchTo(account);
    }

    @Override
    public void setPublisherId(Long publisherId) {
        this.accountId = publisherId;
    }
}

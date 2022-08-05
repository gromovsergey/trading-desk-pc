package com.foros.action.site;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.PublisherSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.session.account.AccountService;
import com.foros.session.site.SiteService;
import com.foros.session.site.TagsService;
import com.foros.util.context.RequestContexts;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;


public class TagsDownloadAction extends BaseActionSupport implements RequestContextsAware, PublisherSelfIdAware {
    private Long id;
    private Long publisherId;
    private Collection<Site> sites;
    private String pageTitle;

    @EJB
    private SiteService siteService;

    @EJB
    private TagsService tagsService;

    @EJB
    private AccountService accountService;
    
    @ReadOnly
    public String downloadTags() throws Exception {
        sites = new ArrayList<Site>();
        if (getId() != null) {
            Site site = siteService.viewSiteFetched(getId());
            if (site != null) {
                sites.add(site);
            } else {
                site = siteService.find(getId());
            }
            publisherId = site.getAccount().getId();
            pageTitle = site.getName();
        } else if (getPublisherId() != null) {
            pageTitle = accountService.findPublisherAccount(publisherId).getName();
            sites.addAll(siteService.getByAccount(publisherId, true));
        } else {
            throw new EntityNotFoundException("Entity with id = null not found");
        }

        for (Site site : sites) {
            tagsService.fetchTagsHtml(site.getTags());
        }

        return SUCCESS;
    }

    public String getGeneratedHtml(Tag tag) {
        return tag.getProperty(TagsService.TAG_VIEW);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
     
    public Long getPublisherId() {
        return publisherId;
    }

    @Override
    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getPublisherContext().switchTo(publisherId);
    }


    public String getPageTitle() {
        return pageTitle;
    }

    public Collection<Site> getSites() {
        return sites;
    }
}

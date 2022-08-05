package com.foros.action.site;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.PublisherSelfIdAware;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.model.time.TimeSpan;
import com.foros.model.time.TimeUnit;
import com.foros.restriction.annotation.Restrict;

import java.util.List;
import javax.persistence.EntityNotFoundException;

public class EditSiteAction extends SiteSupportAction implements PublisherSelfIdAware, BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction = "PublisherEntity.create", parameters = "find('Account', #target.site.account.id)")
    public String create() {
        prepareCreate();
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="PublisherEntity.update", parameters="find('Site',#target.site.id)")
    public String edit() {
        if (site.getId() == null) {
            throw new EntityNotFoundException("Site with id = null not found");
        }
        site = siteService.view(site.getId());
        prepareEdit();
        breadcrumbs = new Breadcrumbs().add(new SiteBreadcrumbsElement(site)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }


    private void prepareEdit() {
        categoryExclusions = null;
        contentCategories = null;
        visualCategories = null;
        tagCategories = null;
    }

    private void prepareCreate() {
        site.setStatus(Status.ACTIVE);
        site.setSiteUrl("http://");
        site.setNoAdsTimeout(0L);
        FrequencyCap fc = new FrequencyCap();
        fc.setPeriodSpan(new TimeSpan(null, TimeUnit.DAY));
        fc.setWindowLengthSpan(new TimeSpan(null, TimeUnit.DAY));
        site.setFrequencyCap(fc);
    }


    @Override
    protected List<SiteCreativeCategoryExclusion> getCategoryExclusions() {
        if (categoryExclusions == null) {
            if (site.getId() != null) {
                categoryExclusions = siteService.getCategoryExclusions(site.getId());
            }
        }
        return categoryExclusions;
    }

    @Override
    public void setPublisherId(Long id) {
        site.setAccount(accountService.findPublisherAccount(id));
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}

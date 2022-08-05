package com.foros.action.site;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.SimpleTextBreadcrumbsElement;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.Site;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.bulk.Paging;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.query.PartialList;
import com.foros.session.site.SiteService;
import com.foros.session.site.TagsService;
import com.foros.session.site.creativeApproval.CreativeExclusionBySiteSelector;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalService;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalTO;
import com.foros.util.CollectionUtils;
import com.foros.util.bean.Filter;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import org.apache.commons.lang3.StringUtils;

public class SearchSiteCreativesApprovalAction extends SiteCreativesApprovalActionSupport implements RequestContextsAware, BreadcrumbsSupport, ModelDriven<SiteCreativesApprovalSearchForm> {
    private static final int PAGE_SIZE = 50;

    @EJB
    private SiteService siteService;

    @EJB
    private TagsService tagService;

    @EJB
    private CreativeSizeService creativeSizeService;

    @EJB
    private SiteCreativeApprovalService siteCreativeApprovalService;

    @EJB
    private CurrentUserService currentUserService;

    private List<CreativeSize> sizes;

    private SiteCreativesApprovalSearchForm searchParams = new SiteCreativesApprovalSearchForm();
    private boolean showPending;

    private PartialList<SiteCreativeApprovalTO> creativeApprovals;

    @ReadOnly
    @Restrict(restriction = "PublisherEntity.viewCreativesApproval", parameters = "#target.prepareSite()")
    public String main() {
        setSite(siteService.find(getSite().getId()));

        return SUCCESS;
    }

    @ReadOnly
    public String search() {
        searchParams.setPageSize(PAGE_SIZE);

        CreativeExclusionBySiteSelector selector = new CreativeExclusionBySiteSelector();
        selector.setSiteId(getSite().getId());
        selector.setDestinationUrl(StringUtils.trimToNull(searchParams.getDestinationUrl()));
        selector.setApprovals(searchParams.getApprovalStatuses());
        selector.setSizeId(searchParams.getSizeId());
        selector.setPaging(new Paging(searchParams.getFirstResultCount(), searchParams.getPageSize()));

        creativeApprovals = siteCreativeApprovalService.searchCreativeApprovals(selector);

        searchParams.setTotal((long) creativeApprovals.getTotal());

        return SUCCESS;
    }

    public void switchContext(RequestContexts contexts) {
        contexts.getPublisherContext().switchTo(getSite().getAccount().getId());
    }

    public Site prepareSite() {
        return siteService.find(getSite().getId());
    }

    public List<CreativeSize> getSizes() {
        if (sizes == null) {
            populateSizes();
        }

        return sizes;
    }

    protected void populateSizes() {
        List<CreativeSize> siteSizes = tagService.findSizesBySite(getSite().getId());

        CollectionUtils.filter(siteSizes, new DeletedEntityFilter<CreativeSize>());
        Collections.sort(siteSizes, new LocalizableNameEntityComparator());

        setSizes(siteSizes);
    }

    private static class DeletedEntityFilter<T extends StatusEntityBase> implements Filter<T> {
        @Override
        public boolean accept(T entity) {
            return entity.getStatus() != Status.DELETED;
        }
    }

    public void setSizes(List<CreativeSize> sizes) {
        this.sizes = sizes;
    }

    public SiteCreativesApprovalSearchForm getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(SiteCreativesApprovalSearchForm searchParams) {
        this.searchParams = searchParams;
    }

    public boolean isShowPending() {
        return showPending;
    }

    public void setShowPending(boolean showPending) {
        this.showPending = showPending;
    }

    public PartialList<SiteCreativeApprovalTO> getCreativeApprovals() {
        return creativeApprovals;
    }

    public void setCreativeApprovals(PartialList<SiteCreativeApprovalTO> creativeApprovals) {
        this.creativeApprovals = creativeApprovals;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new SiteBreadcrumbsElement(getSite()))
                .add(new SimpleTextBreadcrumbsElement("site.creativesApproval"));
    }

    @Override
    public SiteCreativesApprovalSearchForm getModel() {
        return searchParams;
    }

}

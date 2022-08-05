package com.foros.action.site;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.security.AccountType;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCategory;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.session.account.AccountService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.site.SiteService;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.StringUtil;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;

public abstract class SiteSupportAction extends BaseActionSupport implements ModelDriven<Site>, RequestContextsAware {
    @EJB
    protected SiteService siteService;

    @EJB
    DisplayCreativeService displayCreativeService;

    @EJB
    CountryService countryService;

    @EJB
    AccountService accountService;

    protected Site site = emptySite();

    protected List<SiteCreativeCategoryExclusion> categoryExclusions;

    protected List<SiteCreativeCategoryExclusion> visualCategories;
    protected List<SiteCreativeCategoryExclusion> contentCategories;
    protected List<SiteCreativeCategoryExclusion> tagCategories;

    private List<SiteCategory> siteCategories;
    private AccountType accountType;

    private Site emptySite() {
        Site site = new Site();
        site.setAccount(new PublisherAccount());
        return site;
    }

    protected abstract List<SiteCreativeCategoryExclusion> getCategoryExclusions();

    private AccountType getAccountType() {
        if (accountType != null) {
            return accountType;
        }
        accountType = accountService.findPublisherAccount(site.getAccount().getId()).getAccountType();
        return accountType;
    }

    public boolean isAllowAdvExclusionApproval() {
        return getAccountType().isAdvExclusionApprovalAllowed();
    }

    public boolean isAllowAdvExclusions() {
        return getAccountType().isAdvExclusionFlag();
    }

    public boolean isAllowFreqCaps() {
         return getAccountType().isFreqCapsFlag();
    }

    public boolean isAllowWDTags() {
        return getAccountType().isWdTagsFlag();
    }

    public List<SiteCategory> getSiteCategories() {
        if (siteCategories == null) {
            siteCategories = countryService.findSiteCategories(accountService.findPublisherAccount(site.getAccount().getId()).getCountry());
        }
        return siteCategories;
    }

    private List<SiteCreativeCategoryExclusion> populateCategories(Collection<SiteCreativeCategoryExclusion> creativeCategoryExclusions, CreativeCategoryType type) {
        List<CreativeCategory> categoriesAll = type != CreativeCategoryType.TAG ? displayCreativeService.findCategoriesByType(type) : null;

        List<SiteCreativeCategoryExclusion> categories = new LinkedList<SiteCreativeCategoryExclusion>();

        if (creativeCategoryExclusions == null) {
            creativeCategoryExclusions = new LinkedList<SiteCreativeCategoryExclusion>();
        }

        for (SiteCreativeCategoryExclusion categoryExclusion : creativeCategoryExclusions) {
            if (type.equals(categoryExclusion.getCreativeCategory().getType())) {
                categories.add(categoryExclusion);
            }
        }

        if (categoriesAll != null) {
            for (SiteCreativeCategoryExclusion categoryExclusion : creativeCategoryExclusions) {
                if (type.equals(categoryExclusion.getCreativeCategory().getType())) {
                    categoriesAll.remove(categoryExclusion.getCreativeCategory());
                }
            }

            for (CreativeCategory category : categoriesAll) {
                SiteCreativeCategoryExclusion categoryExclusion = new SiteCreativeCategoryExclusion();
                categoryExclusion.setCreativeCategory(category);
                categoryExclusion.setApproval(CategoryExclusionApproval.ACCEPT);
                categories.add(categoryExclusion);
            }
        }

        for (SiteCreativeCategoryExclusion categoryExclusion : categories) {
            CreativeCategory exclusion = categoryExclusion.getCreativeCategory();
            if (StringUtil.isPropertyEmpty(exclusion.getDefaultName())) {
                CreativeCategory category = displayCreativeService.findCategory(exclusion.getId());
                if (category != null) {
                    categoryExclusion.getCreativeCategory().setDefaultName(category.getDefaultName());
                }
            }
        }

        Collections.sort(categories, new Comparator<SiteCreativeCategoryExclusion>() {
            @Override
            public int compare(SiteCreativeCategoryExclusion o1, SiteCreativeCategoryExclusion o2) {
                return LocalizableNameUtil.getComparator().compare(o1.getCreativeCategory().getName(), o2.getCreativeCategory().getName());
            }
        });

        return categories;
    }

    @Override
    public Site getModel(){
        return site;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Collection<SiteCreativeCategoryExclusion> getVisualCategories() {
        if (visualCategories == null) {
            visualCategories = populateCategories(getCategoryExclusions(), CreativeCategoryType.VISUAL);
        }

        return visualCategories;
    }

    public void setVisualCategories(List<SiteCreativeCategoryExclusion> visualCategories) {
        this.visualCategories = visualCategories;
    }

    public Collection<SiteCreativeCategoryExclusion> getContentCategories() {
        if (contentCategories == null) {
            contentCategories = populateCategories(getCategoryExclusions(), CreativeCategoryType.CONTENT);
        } else if (hasErrors() && getFieldErrors().containsKey("siteLevelCreativeExclusionError")
                && !accountService.find(site.getAccount().getId()).getAccountType().isAdvExclusionApprovalAllowed()) {
            for (SiteCreativeCategoryExclusion exclusion : contentCategories) {
                if (exclusion.getApproval() == CategoryExclusionApproval.APPROVAL) {
                    exclusion.setApproval(CategoryExclusionApproval.ACCEPT);
                }
            }
        }

        return contentCategories;
    }

    public void setContentCategories(List<SiteCreativeCategoryExclusion> contentCategories) {
        this.contentCategories = contentCategories;
    }

    public Collection<SiteCreativeCategoryExclusion> getTagCategories() {
        if (tagCategories == null) {
            tagCategories = populateCategories(getCategoryExclusions(), CreativeCategoryType.TAG);
        }

        return tagCategories;
    }

    public void setTagCategories(List<SiteCreativeCategoryExclusion> tagCategories) {
        this.tagCategories = tagCategories;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getPublisherContext().switchTo(getSite().getAccount().getId());
    }
}

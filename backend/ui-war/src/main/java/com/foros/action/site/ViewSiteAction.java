package com.foros.action.site;

import com.foros.framework.ReadOnly;
import com.foros.model.Status;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.session.site.TagsService;
import com.foros.session.site.WDTagService;
import com.foros.session.site.WDTagTO;
import com.foros.util.CollectionUtils;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.StringUtil;
import com.foros.util.bean.Filter;

import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;

public class ViewSiteAction extends SiteSupportAction {

    @EJB
    private WDTagService wdTagService;

    @EJB
    private TagsService tagService;

    private List<WDTagTO> siteWDTags;
    private List<Tag> siteTags;

    @ReadOnly
    public String view() {
        site = siteService.view(site.getId());
        return SUCCESS;
    }

    public List<WDTagTO> getSiteWDTags() {
        if (siteWDTags == null) {
            siteWDTags =wdTagService.findBySite(site.getId());
        }
        return siteWDTags;
    }

    public void setSiteWDTags(List<WDTagTO> siteWDTags) {
        this.siteWDTags = siteWDTags;
    }

    public List<Tag> getSiteTags() {
        if (siteTags == null) {
            siteTags = tagService.findBySite(site.getId());
            for (Tag tag : siteTags) {
                CollectionUtils.filter(tag.getTagPricings(), new Filter<TagPricing>() {
                    @Override
                    public boolean accept(TagPricing element) {
                        return Status.DELETED != element.getStatus();
                    }
                });
            }
        }
        return siteTags;
    }

    public void setSiteTags(List<Tag> siteTags) {
        this.siteTags = siteTags;
    }

    @Override
    protected List<SiteCreativeCategoryExclusion> getCategoryExclusions() {
        if (categoryExclusions == null) {
            categoryExclusions = siteService.getCategoryExclusions(site.getId());
        }
        return categoryExclusions;
    }

    private String getCategories(Collection<SiteCreativeCategoryExclusion> categories, CategoryExclusionApproval approval, boolean isShort) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        boolean allEquals = true;
        for (SiteCreativeCategoryExclusion c: categories) {
            if (c.getApproval() == approval) {
                index++;
                if (!isShort || index <= 10) {
                    if (index > 1) {
                        sb.append(", ");
                    }
                    sb.append(LocalizableNameUtil.getLocalizedValue(c.getCreativeCategory().getName()));
                }
            } else {
                allEquals = false;
            }
        }
        if (allEquals) {
            return StringUtil.getLocalizedString("site.allCategories");
        }
        return sb.toString();
    }

    public String getVisualCategoriesAcceptShort() {
        return getCategories(getVisualCategories(), CategoryExclusionApproval.ACCEPT, true);
    }

    public String getVisualCategoriesAcceptFull() {
        return getCategories(getVisualCategories(), CategoryExclusionApproval.ACCEPT, false);
    }

    public String getVisualCategoriesRejectShort() {
        return getCategories(getVisualCategories(), CategoryExclusionApproval.REJECT, true);
    }

    public String getVisualCategoriesRejectFull() {
        return getCategories(getVisualCategories(), CategoryExclusionApproval.REJECT, false);
    }

    public String getContentCategoriesAcceptShort() {
        return getCategories(getContentCategories(), CategoryExclusionApproval.ACCEPT, true);
    }

    public String getContentCategoriesAcceptFull() {
        return getCategories(getContentCategories(), CategoryExclusionApproval.ACCEPT, false);
    }

    public String getContentCategoriesApprovalShort() {
        return getCategories(getContentCategories(), CategoryExclusionApproval.APPROVAL, true);
    }

    public String getContentCategoriesApprovalFull() {
        return getCategories(getContentCategories(), CategoryExclusionApproval.APPROVAL, false);
    }

    public String getContentCategoriesRejectShort() {
        return getCategories(getContentCategories(), CategoryExclusionApproval.REJECT, true);
    }

    public String getContentCategoriesRejectFull() {
        return getCategories(getContentCategories(), CategoryExclusionApproval.REJECT, false);
    }

}

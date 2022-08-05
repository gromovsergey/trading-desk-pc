package com.foros.action.site;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.ApproveStatus;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.model.site.SiteCreativeCategoryExclusionPK;
import com.foros.util.CollectionUtils;
import com.foros.util.DuplicatesFilter;
import com.foros.util.StringUtil;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Validations(
    conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName = "noAdsTimeout", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.periodSpan.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowLengthSpan.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowCount", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.lifeCount", key = "errors.field.integer")
    }
)
public class SaveSiteAction extends SiteSupportAction implements RequestContextsAware, BreadcrumbsSupport {

    private List<String> selectedTags = new LinkedList<String>();

    @Validate(validation = "Site.create", parameters = "#target.prepareModel()")
    public String create() {
        prepareModel();
        siteService.create(site);
        return SUCCESS;
    }

    @Validate(validation = "Site.update", parameters = "#target.prepareModel()")
    public String update() {
        prepareModel();
        siteService.update(site);
        return SUCCESS;
    }

    public Site prepareModel() {
        site.setCategoryExclusions(getSelectedCategoryExclusions());
        return site;
    }

    private Set<SiteCreativeCategoryExclusion> getSelectedCategoryExclusions() {
        Set<SiteCreativeCategoryExclusion> categories = new LinkedHashSet<SiteCreativeCategoryExclusion>();
        categories.addAll(unpackCCExclusions(visualCategories));
        categories.addAll(unpackCCExclusions(contentCategories));
        categories.addAll(unpackExcludedTags());
        return categories;
    }


    @Override
    protected List<SiteCreativeCategoryExclusion> getCategoryExclusions() {
        if (hasErrors()) {
            categoryExclusions  = new ArrayList<SiteCreativeCategoryExclusion>(getSelectedCategoryExclusions());
            return categoryExclusions;
        }
        if (categoryExclusions == null) {
            if (site.getId() != null) {
                categoryExclusions = siteService.getCategoryExclusions(site.getId());
            }
        }
        return categoryExclusions;
    }

    private Set<SiteCreativeCategoryExclusion> unpackCCExclusions(List<SiteCreativeCategoryExclusion> ccExclusions) {
        Set<SiteCreativeCategoryExclusion> categories = new LinkedHashSet<SiteCreativeCategoryExclusion>();
        if (ccExclusions!=null) {
            for (SiteCreativeCategoryExclusion scce : ccExclusions) {
                if (scce.getApproval() != CategoryExclusionApproval.ACCEPT) {
                    if (site.getId() != null) {
                        SiteCreativeCategoryExclusionPK pk = new SiteCreativeCategoryExclusionPK(scce.getCreativeCategory().getId(), site.getId());
                        scce.setSiteCreativeCategoryExclusionPK(pk);
                    }
                    CreativeCategory category = displayCreativeService.findCategory(scce.getCreativeCategory().getId());
                    if (category != null) {
                        scce.setCreativeCategory(category);
                        categories.add(scce);
                    }
                }
            }
        }

        return categories;
    }

    private Collection<SiteCreativeCategoryExclusion> unpackExcludedTags() {
        Collection<SiteCreativeCategoryExclusion> categories = new LinkedList<SiteCreativeCategoryExclusion>();

        if (selectedTags != null) {
            for (String tagName : selectedTags) {
                tagName = tagName.toLowerCase();
                if (StringUtil.isPropertyEmpty(tagName)) {
                    continue;
                }

                CreativeCategory tagCC = displayCreativeService.findCategory(CreativeCategoryType.TAG, tagName, true);
                if (tagCC == null) {
                    tagCC = new CreativeCategory(null, tagName);
                    tagCC.setType(CreativeCategoryType.TAG);
                    tagCC.setQaStatus(ApproveStatus.HOLD.getLetter());
                }

                SiteCreativeCategoryExclusion scce = new SiteCreativeCategoryExclusion();
                if (site.getId() != null && tagCC.getId() != null) {
                    SiteCreativeCategoryExclusionPK exclusion = new SiteCreativeCategoryExclusionPK(tagCC.getId(), site.getId());
                    scce.setSiteCreativeCategoryExclusionPK(exclusion);
                }

                scce.setApproval(CategoryExclusionApproval.REJECT);
                scce.setCreativeCategory(tagCC);
                categories.add(scce);
            }
        }

        CollectionUtils.filter(categories, new DuplicatesFilter<SiteCreativeCategoryExclusion>("creativeCategory.name", true));
        return categories;
    }

    public List<String> getSelectedTags() {
        return selectedTags;
    }

    public void setSelectedTags(List<String> selectedTags) {
        this.selectedTags = selectedTags;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        if (site.getId() != null) {
            final Site persistent = siteService.find(site.getId());
            return new Breadcrumbs().add(new SiteBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        }
        return null;
    }
}

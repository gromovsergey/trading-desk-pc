package com.foros.action.site;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.Status;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.model.site.TagsCreativeCategoryExclusion;
import com.foros.model.site.TagsCreativeCategoryExclusionPK;
import com.foros.model.template.Option;
import com.foros.restriction.RestrictionService;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.util.StringUtil;
import com.foros.validation.annotation.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(conversionErrorFields = { @ConversionErrorFieldValidator(fieldName = "otherCpm", key = "errors.cost", shortCircuit = true) },
        requiredFields = { @RequiredFieldValidator(key = "errors.field.required", fieldName = "sizeType")})
public class SaveTagAction extends EditSaveTagActionBase implements BreadcrumbsSupport {

    @EJB
    private DisplayCreativeService displayCreativeService;
    @EJB
    private RestrictionService restrictionService;

    private boolean isExpandColoursSection;

    private boolean isExpandLayoutSection;

    private boolean isExpandFontAndSizeSection;

    public boolean isExpandColoursSection() {
        return isExpandColoursSection;
    }

    public boolean isExpandLayoutSection() {
        return isExpandLayoutSection;
    }

    public boolean isExpandFontAndSizeSection() {
        return isExpandFontAndSizeSection;
    }

    public String getPathExpression(int optionId) {
        Option option = optionService.findById(Long.valueOf(optionId));
        String token = option.getToken();
        return "errorOption['" + token + "']";
    }

    @Validate(validation = "Tag.update", parameters = "#target.prepare()")
    public String update() {
        prepare();
        tagsService.update(getTag());
        return SUCCESS;
    }

    @Validate(validation = "Tag.create", parameters = "#target.prepare()")
    public String create() {
        prepare();
        tagsService.create(getTag());
        return SUCCESS;
    }

    public Tag prepare() {
        getTag().setSite(siteService.find(getTag().getSite().getId()));
        getTag().setSizeType(typeService.find(getTag().getSizeType().getId()));

        if (!getTag().isInventoryEstimationFlag()) {

            getPricings().removeAll(Collections.<TagPricing>singleton(null));

            for (TagPricing tp : getPricings()) {
                tp.setStatus(Status.ACTIVE);
                if (tp.getCountry() != null && StringUtil.isPropertyEmpty(tp.getCountry().getCountryCode())) {
                    tp.setCountry(null);
                }
            }
            getTag().setTagPricings(getPricings());
        } else {
            getTag().setTagPricings(new ArrayList<TagPricing>());
        }
        prepareCreativeCategoryExclusions();

        getTag().setMarketplaceType(getMarketplaceTypeTO().getEnum());
        prepareContentCategories();
        if (!getTag().isAllSizesFlag()) {
            getTag().registerChange("flags");
        }
        prepareSizes();

        TagHelper.preparePassback(getTag());

        return getTag();
    }

    private void prepareSizes() {
        getTag().setSizes(new LinkedHashSet<CreativeSize>());

        if (!getTag().isAllSizesFlag()) {
            List<Long> list = getSelectedSizes();
            for (Long id : list) {
                CreativeSize creativeSize = creativeSizeService.findById(id);
                getTag().getSizes().add(creativeSize);
            }
        }
    }

    private void prepareContentCategories() {
        if (getTag().getId() != null && !restrictionService.isPermitted("PublisherEntity.advanced")) {
            getTag().setContentCategories(tagsService.viewFetchedForEdit(getTag().getId()).getContentCategories());
        }
        if (getTag().getContentCategories().isEmpty()) {
            getTag().setContentCategories(new HashSet<ContentCategory>(getTagContentCategories()));
        }
    }

    private void prepareCreativeCategoryExclusions() {
        getTag().setTagsExclusions(new LinkedHashSet<TagsCreativeCategoryExclusion>());

        if (getTag().isTagLevelExclusionFlag()) {
            List<CreativeExclusionTO> list = getCategoryExclusions();
            Long tagId = getTag().getId();
            Set<TagsCreativeCategoryExclusion> categories = getTag().getTagsExclusions();

            for (CreativeExclusionTO exclusionTo : list) {
                CreativeCategory category = displayCreativeService.findCategory(exclusionTo.getId());

                if (category != null) {
                    TagsCreativeCategoryExclusion categoryExclusion = new TagsCreativeCategoryExclusion();

                    if (tagId != null) {
                        categoryExclusion.setTagsCreativeCategoryExclusionPK(new TagsCreativeCategoryExclusionPK(exclusionTo.getId(), tagId));
                    }

                    categoryExclusion.setApproval(CategoryExclusionApproval.valueOf(exclusionTo.getApproval().charAt(0)));

                    categoryExclusion.setCreativeCategory(category);
                    categories.add(categoryExclusion);
                }
            }
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        if (getTag().getId() != null) {
            final Tag persistent = tagsService.find(getTag().getId());
            breadcrumbs.add(new SiteBreadcrumbsElement(persistent.getSite())).add(new TagBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs.add(new SiteBreadcrumbsElement(getTag().getSite())).add("site.breadcrumbs.createTag");
        }
        return breadcrumbs;
    }
}

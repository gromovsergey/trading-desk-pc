package com.foros.action.site;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.PassbackType;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCreativeCategoryExclusion;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.model.site.TagsCreativeCategoryExclusion;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.site.SiteService;
import com.foros.session.site.TagsService;
import com.foros.session.template.OptionService;
import com.foros.session.template.TemplateService;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;
import com.foros.util.UrlUtil;
import com.foros.util.bean.Filter;
import com.foros.util.context.RequestContexts;
import com.foros.util.mapper.Mapper;
import com.foros.util.mapper.Pair;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.comparators.NullComparator;

import javax.ejb.EJB;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public abstract class TagSupportAction extends BaseActionSupport implements ModelDriven<Tag> {

    public static class LocalizedCountryNameComparator implements Comparator<Country> {
        @Override
        public int compare(Country c1, Country c2) {
            String thisCountryName = StringUtil.getLocalizedString("global.country." + c1.getCountryCode() + ".name");
            String thatCountryName = StringUtil.getLocalizedString("global.country." + c2.getCountryCode() + ".name");
            return thisCountryName.compareTo(thatCountryName);
        }
    }

    private Tag tag = emptyTag();
    private Boolean exclusionFlagAccountLevel;
    private static DeletedEntityFilter deletedEntityFilter = new DeletedEntityFilter();
    private List<TagsCreativeCategoryExclusion> availableCreativeCategoryExclusions;
    private PublisherAccount account;
    private List<TagPricing> pricings;
    private String currencyCode;

    @EJB
    protected TagsService tagsService;
    @EJB
    protected SiteService siteService;
    @EJB
    private DisplayCreativeService displayСreativeService;
    @EJB
    protected TemplateService templateService;
    @EJB
    protected OptionService optionService;
    @EJB
    protected CreativeSizeService creativeSizeService;

    @Override
    public Tag getModel() {
        return tag;
    }

    public String getEntityType() {
        return "Tag";
    }

    private Tag emptyTag() {
        Tag tag = new Tag();
        tag.setSite(new Site());
        return tag;
    }

    public String getCurrencyCode() {
        if (currencyCode == null) {
            currencyCode = getAccount().getCurrency().getCurrencyCode();
        }
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<TagPricing> getPricings() {
        if (pricings == null) {
            //noinspection unchecked
            pricings = new ArrayList<TagPricing>(org.apache.commons.collections.CollectionUtils.select(tag.getTagPricings(), new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return Status.DELETED != ((TagPricing) object).getStatus();
                }
            }));

            /**
             * Sort TagPricings by country name, ccgType and ccgRateType values
             * correspondingly so that null values go first and further elements
             * in ascending order.
             */
            Collections.sort(pricings, new Comparator<TagPricing>() {
                @Override
                public int compare(TagPricing o1, TagPricing o2) {
                    int compareCountries = new NullComparator(new LocalizedCountryNameComparator(), false).compare(o1.getCountry(), o2.getCountry());
                    if (compareCountries != 0) {
                        return compareCountries;
                    }

                    int compareCCGTypes = new NullComparator().compare(o2.getCcgType(), o1.getCcgType());
                    if (compareCCGTypes != 0) {
                        return compareCCGTypes;
                    }

                    int compareRateTypes = new NullComparator().compare(o2.getCcgRateType(), o1.getCcgRateType());
                    return compareRateTypes;
                }
            });
        }
        return pricings;
    }

    public void setPricings(List<TagPricing> pricings) {
        this.pricings = pricings;
    }

    protected PublisherAccount getAccount() {
        if (account == null) {
            account = siteService.find(tag.getSite().getId()).getAccount();
        }
        return account;
    }

    /**
     * common implementation for switch context feature. utilized by edit and
     * view actions
     *
     * @param contexts
     */
    public void switchContext(RequestContexts contexts) {
        contexts.getPublisherContext().switchTo(getTag().getAccount().getId());
    }

    public List<TagsCreativeCategoryExclusion> getAvailableCreativeCategoryExclusions() {
        if (availableCreativeCategoryExclusions == null) {
            populateCategoryExclusions();
        }
        return availableCreativeCategoryExclusions;
    }

    public void setAvailableCreativeCategoryExclusions(
            List<TagsCreativeCategoryExclusion> availableCreativeCategoryExclusions) {
        this.availableCreativeCategoryExclusions = availableCreativeCategoryExclusions;
    }

    public Boolean isExclusionFlagAccountLevel() {
        if (exclusionFlagAccountLevel == null) {
            populateFlags();
        }

        return exclusionFlagAccountLevel;
    }

    public void setExclusionFlagAccountLevel(Boolean exclusionFlagAccountLevel) {
        this.exclusionFlagAccountLevel = exclusionFlagAccountLevel;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @SuppressWarnings("rawtypes")
    public static DeletedEntityFilter getDeletedEntityFilter() {
        return deletedEntityFilter;
    }

    protected void populatePassback() {
        Tag tag = getTag();
        String passback = tag.getPassback();

        if (StringUtil.isPropertyNotEmpty(passback) && tag.getPassbackType() != PassbackType.HTML_URL) {
            try {
                tag.setPassbackHtml(tagsService.getPassbackHtml(tag));
            } catch (IOException e) {
                tag.setPassbackHtml(null);
                addFieldError("file.passback", getText("errors.fileexist", new String[] { tag.getPassback() }));
            }
        }
    }

    protected void populateCategoryExclusions() {
        setAvailableCreativeCategoryExclusions(fillCategories());

        if (hasErrors()) {
            updateExclusionApprovals();
        }
    }

    private void updateExclusionApprovals() {
        Map<Long, TagsCreativeCategoryExclusion> exclusions = CollectionUtils.map(
                new Mapper<TagsCreativeCategoryExclusion, Long, TagsCreativeCategoryExclusion>() {
                    @Override
                    public Pair<Long, TagsCreativeCategoryExclusion> item(TagsCreativeCategoryExclusion value) {
                        return new Pair<Long, TagsCreativeCategoryExclusion>(value.getCreativeCategory().getId(), value);
                    }
                }, getTag().getTagsExclusions());

        for (TagsCreativeCategoryExclusion existingExclusion : getAvailableCreativeCategoryExclusions()) {
            TagsCreativeCategoryExclusion exclusion = exclusions.get(existingExclusion.getCreativeCategory().getId());

            if (exclusion != null) {
                existingExclusion.setApproval(exclusion.getApproval());
            }
        }
    }

    protected void populateFlags() {
        setExclusionFlagAccountLevel(getAccount().getAccountType().isAdvExclusionSiteTagFlag());
    }

    private void sortCategories(List<TagsCreativeCategoryExclusion> categories) {
        Collections.sort(categories, new Comparator<TagsCreativeCategoryExclusion>() {
            @Override
            public int compare(TagsCreativeCategoryExclusion o1, TagsCreativeCategoryExclusion o2) {
                return StringUtil.compareToIgnoreCase(o1.getCreativeCategory().getDefaultName(), o2
                        .getCreativeCategory().getDefaultName());
            }
        });
    }

    private List<TagsCreativeCategoryExclusion> fillCategories() {
        List<CreativeCategory> categoriesAll = displayСreativeService.findCategoriesByType(CreativeCategoryType.VISUAL);
        List<TagsCreativeCategoryExclusion> categories = new ArrayList<TagsCreativeCategoryExclusion>();

        if (getTag().getTagsExclusions().isEmpty()) {
            manageSiteCreativeCategoryExclusion(categoriesAll, categories);
        }

        if (getTag().getId() != null) {
            for (TagsCreativeCategoryExclusion tagCategoryExclusion : getTag().getTagsExclusions()) {
                categories.add(tagCategoryExclusion);
                categoriesAll.remove(tagCategoryExclusion.getCreativeCategory());
            }
        }

        for (CreativeCategory category : categoriesAll) {
            TagsCreativeCategoryExclusion categoryExclusion = new TagsCreativeCategoryExclusion();
            categoryExclusion.setCreativeCategory(category);
            categoryExclusion.setApproval(CategoryExclusionApproval.ACCEPT);
            categories.add(categoryExclusion);
        }

        for (TagsCreativeCategoryExclusion categoryExclusion : categories) {
            CreativeCategory exclusion = categoryExclusion.getCreativeCategory();
            if (StringUtil.isPropertyEmpty(exclusion.getDefaultName())) {
                CreativeCategory category = displayСreativeService.findCategory(exclusion.getId());
                if (category != null) {
                    categoryExclusion.getCreativeCategory().setDefaultName(category.getDefaultName());
                }
            }
        }

        sortCategories(categories);

        return categories;
    }

    private static class DeletedEntityFilter<T extends StatusEntityBase> implements Filter<T> {
        @Override
        public boolean accept(T entity) {
            return entity.getStatus() != Status.DELETED;
        }
    }

    private void manageSiteCreativeCategoryExclusion(List<CreativeCategory> categoriesAll,
            List<TagsCreativeCategoryExclusion> categories) {
        Collection<SiteCreativeCategoryExclusion> siteCreativeCategoryExclusions = siteService
                .getCategoryExclusions(getTag().getSite().getId());
        for (SiteCreativeCategoryExclusion siteCategoryExclusion : siteCreativeCategoryExclusions) {
            if (CreativeCategoryType.VISUAL == siteCategoryExclusion.getCreativeCategory().getType()) {
                TagsCreativeCategoryExclusion categoryExclusion = new TagsCreativeCategoryExclusion();
                categoryExclusion.setCreativeCategory(siteCategoryExclusion.getCreativeCategory());
                categoryExclusion.setApproval(siteCategoryExclusion.getApproval());
                categories.add(categoryExclusion);
                categoriesAll.remove(siteCategoryExclusion.getCreativeCategory());
            }
        }
    }

    public boolean isUrl(String url) {
        return UrlUtil.isSchemaUrl(url, true);
    }

    protected void setAllSizesToTag() {
        List<CreativeSize> allSizes = creativeSizeService.findByAccountTypeAndSizeType(getTag().getAccount().getAccountType().getId(), getTag().getSizeType().getId());
        // filter out deleted creative sizes
        Collections.sort(allSizes, new LocalizableNameEntityComparator());
        getTag().setSizes(new LinkedHashSet<>(allSizes));
    }

    public List<CreativeSize> getSortedSizes() {
        List<CreativeSize> sizes = new ArrayList<>(getTag().getEffectiveSizes());
        Collections.sort(sizes, new LocalizableNameEntityComparator());
        return sizes;
    }

}

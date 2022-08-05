package com.foros.action.site;

import static com.foros.model.Status.ACTIVE;
import static com.foros.util.StringUtil.isPropertyNotEmpty;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.security.AccountType;
import com.foros.model.site.PassbackType;
import com.foros.model.site.SiteRate;
import com.foros.model.site.SiteRateType;
import com.foros.model.site.TagPricing;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.util.CollectionUtils;
import com.foros.util.mapper.Converter;

import static java.util.Arrays.asList;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;

public class EditTagAction extends EditSaveTagActionBase implements BreadcrumbsSupport {

    private Breadcrumbs breadcrumbs = new Breadcrumbs();

    @ReadOnly
    @Restrict(restriction = "PublisherEntity.create", parameters = "find('Site', #target.tag.site.id)")
    public String create() throws Exception {
        if (getTag().getSite() == null || getTag().getSite().getId() == null) {
            throw new EntityNotFoundException("Site with id = null not found");
        }

        getTag().setSite(siteService.find(getTag().getSite().getId()));
        getTag().setStatus(ACTIVE);
        getTag().setPassbackType(PassbackType.HTML_URL);
        getTag().setTagPricings(Collections.singletonList(new TagPricing()));
        prepare();

        breadcrumbs.add(new SiteBreadcrumbsElement(getTag().getSite())).add("site.breadcrumbs.createTag");

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "PublisherEntity.update", parameters = "find('Tag', #target.tag.id)")
    public String edit() throws Exception {
        if (getTag().getId() == null) {
            throw new EntityNotFoundException("Tag with id = null not found");
        }

        setTag(tagsService.viewFetchedForEdit(getTag().getId()));

        if (getTag().getTagPricings().isEmpty()) {
            getTag().setTagPricings(Collections.singletonList(new TagPricing()));
        }

        prepare();

        breadcrumbs.add(new SiteBreadcrumbsElement(getTag().getSite())).add(new TagBreadcrumbsElement(getTag())).add(ActionBreadcrumbs.EDIT);

        return SUCCESS;
    }

    @Override
    protected void populatePassback() {
        super.populatePassback();

        if (isPropertyNotEmpty(getTag().getPassbackHtml())
                || getFieldErrors().keySet().containsAll(asList("version", "file.passback"))) {
            getTag().setPassback(null);
        }
    }

    private void prepare() {
        setSelectedContentCategories(null);
        setPricings(null);
        populateMarketPlaceType();

        if (getTag().getId() != null) {
            populatePassback();
        }

        fillRevenueShare();
    }

    private void populateMarketPlaceType() {
        if (getTag().getId() == null) {
            // create case
            if (isWalledGardenEnabled()) {
                marketplaceTypeTO.setEnum(wgService.findByPublisher(getAccount().getId()).getPublisherMarketplaceType());
            }
        } else {
            // update case
            if (getTag().getMarketplaceType() != null) {
                marketplaceTypeTO.setEnum(getTag().getMarketplaceType());
            }
        }
    }

    private void fillRevenueShare() {
        if (!isInternal()) {
            Map<String, BigDecimal> oldRevenueShare = getPrevRevenueShare();
            for (TagPricing pricing : getPricings()) {
                SiteRate siteRate = pricing.getSiteRate();
                if (siteRate != null && siteRate.getRateType() == SiteRateType.RS) {
                    String key = (pricing.getCountry() == null ? "" : pricing.getCountry().getCountryCode()) +
                            (pricing.getCcgType() == null ? "" : pricing.getCcgType().name()) +
                            (pricing.getCcgRateType() == null ? "" : pricing.getCcgRateType().name());
                    oldRevenueShare.put(key, siteRate.getRatePercent());
                }
            }
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }

    @ReadOnly
    public String changeSizeType() {
        AccountType accountType = siteService.find(getTag().getSite().getId()).getAccount().getAccountType();
        List<CreativeSize> allSizes = creativeSizeService.findByAccountTypeAndSizeType(accountType.getId(), getTag().getSizeType().getId());
        Collections.sort(allSizes, new LocalizableNameEntityComparator());
        setSizes(allSizes);
        SizeType sizeType = typeService.find(getTag().getSizeType().getId());
        if (sizeType.isMultiSize()) {
            return "multisize";
        } else {
            return "singlesize";
        }


    }

    @Override
    public List<Long> getSelectedSizes() {
        if (getTag().getId() == null || !selectedSizes.isEmpty()) {
            return selectedSizes;
        }

        if (getTag().isAllSizesFlag()) {
            setAllSizesToTag();
        }

        selectedSizes.addAll(CollectionUtils.convert(new Converter<CreativeSize, Long>() {
            @Override
            public Long item(CreativeSize value) {
                return value.getId();
            }
        }, getTag().getSizes()));
        return selectedSizes;
    }

}

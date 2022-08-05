package com.foros.action.site;

import com.foros.cache.application.CountryCO;
import com.foros.framework.MarketplaceTypeTO;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.ContentCategory;
import com.foros.session.admin.country.CountryService;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.creative.SizeTypeService;
import com.foros.session.creative.SizeTypeTO;
import com.foros.util.CollectionUtils;
import com.foros.util.CountryHelper;
import com.foros.util.mapper.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;

public abstract class EditSaveTagActionBase extends TagSupportAction  implements RequestContextsAware {
    private List<CreativeExclusionTO> categoryExclusions = new ArrayList<CreativeExclusionTO>();
    private List<CreativeSize> sizes;
    protected List<Long> selectedSizes = new ArrayList<>();
    private Boolean walledGardenEnabled;
    protected MarketplaceTypeTO marketplaceTypeTO = new MarketplaceTypeTO();
    private Boolean allowInventoryEstimation;
    private List<ContentCategory> availableContentCategories;
    private List<ContentCategory> tagContentCategories;
    private List<Long> selectedContentCategories;
    private List<CountryCO> countries;
    private List<SizeTypeTO> types;

    @EJB
    protected WalledGardenService wgService;
    @EJB
    private CountryService countryService;
    @EJB
    protected CreativeSizeService creativeSizeService;
    @EJB
    protected SizeTypeService typeService;

    private Map<String, BigDecimal> prevRevenueShare = new HashMap<>();

    public List<CreativeExclusionTO> getCategoryExclusions() {

        return categoryExclusions;
    }

    public void setCategoryExclusions(List<CreativeExclusionTO> categoryExclusions) {
        this.categoryExclusions = categoryExclusions;
    }

    public List<CreativeSize> getSizes() {
        if (sizes == null) {
            if (getTypes().isEmpty()) {
                sizes = new ArrayList<CreativeSize>();
            } else {
                populateSizes(getAccount().getAccountType().getId(), getTag().getSizeType().getId());
            }
        }

        return sizes;
    }

    public void setSizes(List<CreativeSize> sizes) {
        this.sizes = sizes;
    }

    public List<Long> getSelectedContentCategories() {
        return selectedContentCategories;
    }

    public void setSelectedContentCategories(List<Long> selectedContentCategories) {
        this.selectedContentCategories = selectedContentCategories;
    }

    public Boolean isWalledGardenEnabled() {
        if (walledGardenEnabled ==null) {
            populateFlags();
        }
        return walledGardenEnabled;
    }

    public void setWalledGardenEnabled(Boolean walledGardenEnabled) {
        this.walledGardenEnabled = walledGardenEnabled;
    }

    public List<CountryCO> getCountries() {
        if (countries == null) {
            setCountries(CountryHelper.sort(countryService.getIndex()));
        }

        return countries;
    }

    public void setCountries(List<CountryCO> countries) {
        this.countries = countries;
    }

    public List<ContentCategory> getTagContentCategories() {
        if (tagContentCategories == null) {
            populateContentCategories();
        }

        return tagContentCategories;
    }

    public void setTagContentCategories(List<ContentCategory> tagContentCategories) {
        this.tagContentCategories = tagContentCategories;
    }

    public List<ContentCategory> getAvailableContentCategories() {
        if (availableContentCategories == null) {
            populateContentCategories();
        }

        return availableContentCategories;
    }

    public void setAvailableContentCategories(List<ContentCategory> availableContentCategories) {
        this.availableContentCategories = availableContentCategories;
    }

    public void setAllowInventoryEstimation(Boolean allowInventoryEstimation) {
        this.allowInventoryEstimation = allowInventoryEstimation;
    }

    public Boolean isAllowInventoryEstimation() {
        if (allowInventoryEstimation == null) {
            populateFlags();
        }

        return allowInventoryEstimation;
    }

    public MarketplaceTypeTO getMarketplaceTypeTO() {
        return marketplaceTypeTO;
    }

    public void setMarketplaceTypeTO(MarketplaceTypeTO marketplaceTypeTO) {
        this.marketplaceTypeTO = marketplaceTypeTO;
    }

    @SuppressWarnings("unchecked")
    protected void populateSizes(Long accountTypeId, Long sizeTypeId) {
        List<CreativeSize> allSizes = creativeSizeService.findByAccountTypeAndSizeType(accountTypeId, sizeTypeId);
        // filter out deleted creative sizes
        Collections.sort(allSizes, new LocalizableNameEntityComparator());

        setSizes(allSizes);
    }

    @Override
    protected void populateFlags() {
        super.populateFlags();

        setAllowInventoryEstimation(getAccount().getAccountType().isPublisherInventoryEstimationFlag());
        setWalledGardenEnabled(wgService.isPublisherWalledGarden(getAccount().getId()));
    }

    protected void populateContentCategories() {
        List<ContentCategory> tagCCs;
        // selected/linked content categories
        if (selectedContentCategories == null || selectedContentCategories.isEmpty()) {
            if (getTag().getId() != null && !getFieldErrors().containsKey("contentCategories")) {
                // load content categories associated with tag
                tagCCs = getTag().getContentCategories().isEmpty() ? new ArrayList<ContentCategory>()
                        : countryService.findContentCategories(getTag().getContentCategories());
            } else {
                tagCCs = new ArrayList<ContentCategory>();
            }
        } else {
            tagCCs = countryService.findContentCategories(
                    new HashSet<ContentCategory>(CollectionUtils.convert(new Converter<Long, ContentCategory>() {
                        @Override
                        public ContentCategory item(Long value) {
                            return new ContentCategory(value, null);
                        }
                    }, selectedContentCategories)));
        }

        setTagContentCategories(tagCCs);

        // available content categories
        setAvailableContentCategories(countryService.findContentCategories(getAccount().getCountry()));
    }

    public boolean isExpandable() {
        Set<CreativeSize> sizes = getTag().getSizes();
        if (getTag().isAllSizesFlag()) {
            sizes.addAll(getSizes());
        }
        for (CreativeSize size : sizes) {
            if (size.isExpandable()) {
                return true;
            }
        }
        return false;
    }

    public Map<String, BigDecimal> getPrevRevenueShare() {
        return prevRevenueShare;
    }

    public List<SizeTypeTO> getTypes() {
        if (types == null) {
            types = typeService.findByAccountType(getAccount().getAccountType().getId());
        }
        return types;
    }

    public void setTypes(List<SizeTypeTO> types) {
        this.types = types;
    }

    public List<Long> getSelectedSizes() {
        return selectedSizes;
    }

    public void setSelectedSizes(List<Long> selectedSizes) {
        this.selectedSizes = selectedSizes;
    }
}

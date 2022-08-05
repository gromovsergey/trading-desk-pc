package com.foros.session.admin.country;

import com.foros.cache.application.CountryCO;
import com.foros.model.AddressField;
import com.foros.model.Country;
import com.foros.model.site.CategoryTO;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.SiteCategory;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

@Local
public interface CountryService {

    List<ContentCategory> findContentCategories(Set<ContentCategory> contentCategories);

    public enum PredefinedAddressField {
        LINE1("Line1"),
        LINE2("Line2"),
        LINE3("Line3"),
        CITY("City"),
        STATE("State"),
        PROVINCE("Province"),
        ZIP("Zip"),
        COUNTRY("Country");
        private String name;

        PredefinedAddressField(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    Country update(Country country, File invoiceRptFile);

    Country find(String countryCode);

    Country findForEdit(String countryCode);

    Country findByCountryId(Long id);

    void refresh(String countryCode);

    Collection<CountryCO> search();

    /**
     * returns address fields for specified country sorted by orderNumber field
     * @param countryCode code fo country to look up address fields for
     * @return address fields for specified country sorted by orderNumber field
     */
    List<AddressField> getAddressFields(String countryCode);

    Collection<CountryCO> getIndex();

    Collection<CountryCO> getIndex(Collection<Long> accountIds);

    /**
     * Return content categories for input country for edit
     * @param country
     * @return collection of ContentCategory entities
     */
    List<CategoryTO> findForEditContentCategories(Country country);

    /**
     * Return tag content categories associated with given country
     * @param country
     * @return
     */
    public List<ContentCategory> findContentCategories(Country country);

    /**
     * * Return site categories for given country for edit
     * @param country
     * @return collection of SiteCategory entities
     */
    List<CategoryTO> findForEditSiteCategories(Country country);

    /**
     * Return site categories associated with given country
     * @param country
     * @return
     */
    public List<SiteCategory> findSiteCategories(Country country);

    ContentCategory findContentCategory(Long id);

    public String getConversionTrackingPixelCode(Country country, Long accountId, Long actionId);

    public String getConversionTrackingNoAudiencePixelCode(Country country, Long accountId, Long actionId);

    public String getImagePixel(Country country, Long accountId, Long actionId);

}

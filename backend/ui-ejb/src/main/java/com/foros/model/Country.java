package com.foros.model;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.CountryEntityChange;
import com.foros.jaxb.adapters.CountryAdapter;
import com.foros.model.currency.Currency;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.site.ContentCategory;
import com.foros.model.site.SiteCategory;
import com.foros.util.NumberUtil;
import com.foros.util.changes.ChangesSupportList;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.FractionDigitsConstraint;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.UrlConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "COUNTRY")
@NamedQueries({
    @NamedQuery(name = "Country.findAll", query =
        "SELECT NEW com.foros.cache.application.CountryCO(c.countryCode, c.sortOrder, cu.id, cu.currencyCode, t.id, t.key, c.countryId) " +
        "FROM Country c  LEFT JOIN c.currency cu LEFT JOIN c.timezone t"),
    @NamedQuery(name = "ContentCategory.findByCountry", query = "SELECT cc FROM ContentCategory cc WHERE cc.country = :country ORDER BY cc.name"),
    @NamedQuery(name = "SiteCategory.findByCountry", query = "SELECT sc FROM SiteCategory sc WHERE sc.country = :country ORDER BY sc.name"),
    @NamedQuery(name = "Country.findByCountryId", query = "SELECT c FROM Country c WHERE c.countryId = :countryId")
})
@XmlJavaTypeAdapter(CountryAdapter.class)
@Audit(nodeFactory = CountryEntityChange.Factory.class)
public class Country extends VersionEntityBase implements Serializable {
    @Id
    @Column(name = "COUNTRY_CODE", nullable = false)
    @RequiredConstraint
    private String countryCode;

    @JoinColumn(name = "CURRENCY_ID", referencedColumnName = "CURRENCY_ID")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @RequiredConstraint
    @HasIdConstraint
    private Currency currency;

    @JoinColumn(name = "TIMEZONE_ID", referencedColumnName = "TIMEZONE_ID")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @RequiredConstraint
    @HasIdConstraint
    private Timezone timezone;

    @Column(name = "LANGUAGE")
    @RequiredConstraint
    private String language;

    @Column(name = "SORTORDER")
    private Long sortOrder;

    @Column(name = "DEFAULT_PAYMENT_TERMS", nullable = false)
    @RequiredConstraint
    @RangeConstraint(min = "14", max = "60")
    private Long defaultPaymentTerms;

    @Column(name = "VAT_ENABLED")
    private boolean vatEnabled;

    @Column(name = "VAT_NUMBER_INPUT_ENABLED")
    private boolean vatNumberInputEnabled;

    @Column(name = "DEFAULT_VAT_RATE", precision = 5, scale = 3)
    private BigDecimal defaultVATRate;

    @Column(name = "DEFAULT_AGENCY_COMMISSION")
    private BigDecimal defaultAgencyCommission;

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToMany(mappedBy = "country", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @OrderBy("orderNumber")
    private List<AddressField> addressFields = new LinkedList<AddressField>();

    @Column(name = "HIGH_CHANNEL_THRESHOLD", nullable = false)
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "9999999999")
    private Long highChannelThreshold = 20L;

    @Column(name = "LOW_CHANNEL_THRESHOLD", nullable = false)
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "9999999999")
    private Long lowChannelThreshold = 10L;

    @Column(name = "MAX_URL_TRIGGER_SHARE", precision = 5, scale = 2, nullable = false)
    @RequiredConstraint
    private BigDecimal maxUrlTriggerShare = BigDecimal.ONE;

    @Column(name = "MIN_URL_TRIGGER_THRESHOLD", nullable = false)
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "9999999999")
    private Long minUrlTriggerThreshold = 0L;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OrderBy("name ASC")
    private Set<SiteCategory> siteCategories = new LinkedHashSet<SiteCategory>();

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OrderBy("name ASC")
    private Set<ContentCategory> contentCategories = new LinkedHashSet<ContentCategory>();

    @Column(name = "COUNTRY_ID", updatable = false, insertable = false)
    @ChangesInspection(type = InspectionType.NONE)
    private Long countryId;

    @Column(name = "AD_FOOTER_URL")
    @RequiredConstraint
    @UrlConstraint(schemas = {"//", "http://", "https://"})
    private String adFooterURL;

    @Column(name = "ADSERVING_DOMAIN")
    private String adservingDomain;

    @Column(name = "AD_TAG_DOMAIN")
    private String adTagDomain;

    @Column(name = "CONVERSION_TAG_DOMAIN")
    @RequiredConstraint
    private String conversionTagDomain;

    @Column(name = "DISCOVER_DOMAIN")
    private String discoverDomain;

    @Column(name = "STATIC_DOMAIN")
    private String staticDomain;

    @Column(name = "MIN_TAG_VISIBILITY")
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "100")
    private Long minRequiredTagVisibility;

    @OneToOne
    @JoinColumn(name = "INVOICE_CUSTOM_REPORT_ID")
    private BirtReport invoiceReport;

    public Country() {
    }

    public Country(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        this.registerChange("countryCode");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getCountryCode() != null ? this.getCountryCode().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Country)) {
            return false;
        }
        Country other = (Country)object;
        if (!ObjectUtils.equals(this.getCountryCode(), other.getCountryCode())) {
            return false;
        }
        return true;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        this.registerChange("currency");
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
        this.registerChange("sortOrder");
    }

    public Timezone getTimezone() {
        return timezone;
    }

    public void setTimezone(Timezone timezone) {
        this.timezone = timezone;
        this.registerChange("timezone");
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        this.registerChange("language");
    }

    public boolean isVatEnabled() {
        return vatEnabled;
    }

    public void setVatEnabled(boolean vatEnabled) {
        this.vatEnabled = vatEnabled;
        this.registerChange("vatEnabled");
    }

    public boolean isVatNumberInputEnabled() {
        return vatNumberInputEnabled;
    }

    public void setVatNumberInputEnabled(boolean vatNumberInputEnabled) {
        this.vatNumberInputEnabled = vatNumberInputEnabled;
        this.registerChange("vatNumberInputEnabled");
    }

    public BigDecimal getDefaultVATRate() {
        return defaultVATRate;
    }

    public void setDefaultVATRate(BigDecimal defaultVATRate) {
        this.defaultVATRate = defaultVATRate;
        this.registerChange("defaultVATRate");
        this.registerChange("defaultVATRateView");
    }

    @Transient
    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(3)
    public BigDecimal getDefaultVATRateView() {
        return NumberUtil.toPercents(getDefaultVATRate());
    }

    public void setDefaultVATRateView(BigDecimal defaultVATRateView) {
        setDefaultVATRate(NumberUtil.fromPercents(defaultVATRateView));
    }

    public BigDecimal getDefaultAgencyCommission() {
        return defaultAgencyCommission;
    }

    public void setDefaultAgencyCommission(BigDecimal defaultAgencyCommission) {
        this.defaultAgencyCommission = defaultAgencyCommission;
        this.registerChange("defaultAgencyCommission");
        this.registerChange("defaultAgencyCommissionView");
    }

    @Transient
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(2)
    public BigDecimal getDefaultAgencyCommissionView() {
        return NumberUtil.toPercents(getDefaultAgencyCommission());
    }

    public void setDefaultAgencyCommissionView(BigDecimal defaultAgencyCommissionView) {
        setDefaultAgencyCommission(NumberUtil.fromPercents(defaultAgencyCommissionView));
    }

    public List<AddressField> getAddressFields() {
        return new ChangesSupportList<AddressField>(this, "addressFields", addressFields);
    }

    public void setAddressFields(List<AddressField> addressFields) {
        this.addressFields = addressFields;
        this.registerChange("addressFields");
    }

    public void setHighChannelThreshold(Long highChannelThreshold) {
        this.highChannelThreshold = highChannelThreshold;
        this.registerChange("highChannelThreshold");
    }

    public Long getHighChannelThreshold() {
        return highChannelThreshold;
    }

    public void setLowChannelThreshold(Long lowChannelThreshold) {
        this.lowChannelThreshold = lowChannelThreshold;
        this.registerChange("lowChannelThreshold");
    }

    public BigDecimal getMaxUrlTriggerShare() {
        return maxUrlTriggerShare;
    }

    public void setMaxUrlTriggerShare(BigDecimal maxUrlTriggerShare) {
        this.maxUrlTriggerShare = maxUrlTriggerShare;
        this.registerChange("maxUrlTriggerShare");
        this.registerChange("maxUrlTriggerShareView");
    }

    @Transient
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "100")
    @FractionDigitsConstraint(2)
    public BigDecimal getMaxUrlTriggerShareView() {
        return NumberUtil.toPercents(getMaxUrlTriggerShare());
    }

    public void setMaxUrlTriggerShareView(BigDecimal maxUrlTriggerShare) {
        setMaxUrlTriggerShare(NumberUtil.fromPercents(maxUrlTriggerShare));
    }

    public Long getMinUrlTriggerThreshold() {
        return minUrlTriggerThreshold;
    }

    public void setMinUrlTriggerThreshold(Long minUrlTriggerThreshold) {
        this.minUrlTriggerThreshold = minUrlTriggerThreshold;
        this.registerChange("minUrlTriggerThreshold");
    }

    public Long getLowChannelThreshold() {
        return lowChannelThreshold;
    }

    public Long getDefaultPaymentTerms() {
        return defaultPaymentTerms;
    }

    public void setDefaultPaymentTerms(Long defaultPaymentTerms) {
        this.defaultPaymentTerms = defaultPaymentTerms;
        this.registerChange("defaultPaymentTerms");
    }

    @Override
    public String toString() {
        return "com.foros.model.Country[" +
                "countryCode=" + getCountryCode() +
                ", timeZone=" + getTimezone() +
                ", language=" + getLanguage() +
                ", sortOrder=" + getSortOrder() +
                "]";
    }

    public Set<SiteCategory> getSiteCategories() {
        return new ChangesSupportSet<SiteCategory>(this, "siteCategories", siteCategories);
    }

    public void setSiteCategories(Set<SiteCategory> siteCategories) {
        this.siteCategories = siteCategories;
        this.registerChange("siteCategories");
    }

    public Set<ContentCategory> getContentCategories() {
        return new ChangesSupportSet<ContentCategory>(this, "contentCategories", contentCategories);
    }

    public void setContentCategories(Set<ContentCategory> contentCategories) {
        this.contentCategories = contentCategories;
        this.registerChange("contentCategories");
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
        this.registerChange("countryId");
    }

    public String getAdFooterURL() {
        return adFooterURL;
    }

    public void setAdFooterURL(String adFooterURL) {
        this.adFooterURL = adFooterURL;
        this.registerChange("adFooterURL");
    }

    public Long getMinRequiredTagVisibility() {
        return minRequiredTagVisibility;
    }

    public void setMinRequiredTagVisibility(Long minRequiredTagVisibility) {
        this.minRequiredTagVisibility = minRequiredTagVisibility;
        this.registerChange("minRequiredTagVisibility");
    }

    public String getAdservingDomain() {
        return adservingDomain;
    }

    public String getAdservingDomainOrDefault(String defaultAdservingDomain) {
        if (StringUtils.isEmpty(adservingDomain)) {
            return defaultAdservingDomain;
        }

        return adservingDomain;
    }

    public String getDiscoverDomain() {
        return discoverDomain;
    }

    public String getDiscoverDomainOrDefault(String defaultDiscoverDomain) {
        if (StringUtils.isEmpty(discoverDomain)) {
            return defaultDiscoverDomain;
        }

        return discoverDomain;
    }

    public String getStaticDomain() {
        return staticDomain;
    }

    public String getStaticDomainOrDefault(String defaultStaticDomain) {
        if (StringUtils.isEmpty(staticDomain)) {
            return defaultStaticDomain;
        }

        return staticDomain;
    }

    public void setAdservingDomain(String adservingDomain) {
        this.adservingDomain = adservingDomain;
        this.registerChange("adservingDomain");
    }

    public void setDiscoverDomain(String discoverDomain) {
        this.discoverDomain = discoverDomain;
        this.registerChange("discoverDomain");
    }

    public void setStaticDomain(String staticDomain) {
        this.staticDomain = staticDomain;
        this.registerChange("staticDomain");
    }

    public String getAdTagDomain() {
        return adTagDomain;
    }

    public void setAdTagDomain(String adTagDomain) {
        this.adTagDomain = adTagDomain;
        this.registerChange("adTagDomain");
    }

    public String getConversionTagDomain() {
        return conversionTagDomain;
    }

    public void setConversionTagDomain(String conversionTagDomain) {
        this.conversionTagDomain = conversionTagDomain;
        this.registerChange("conversionTagDomain");
    }

    public BirtReport getInvoiceReport() {
        return invoiceReport;
    }

    public void setInvoiceReport(BirtReport invoiceReport) {
        this.invoiceReport = invoiceReport;
        this.registerChange("invoiceReport");
    }
}

package com.foros.model.site;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.entity.TagPricingAuditSerializer;
import com.foros.model.Country;
import com.foros.model.Identifiable;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.RateType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Entity class TagPricing
 *
 * @author vladimir
 */
@Entity
@Table(name = "TAGPRICING")
@AllowedStatuses(values = { Status.ACTIVE, Status.DELETED })
@Audit(serializer = TagPricingAuditSerializer.class)
public class TagPricing extends StatusEntityBase implements Serializable, Identifiable {

    @GenericGenerator(name = "TagPricingGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator",
            parameters = {
                    @Parameter(name = "allocationSize", value = "20"),
                    @Parameter(name = "sequenceName", value = "TAGPRICING_TAG_PRICING_ID_SEQ") })
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TagPricingGen")
    @Column(name = "TAG_PRICING_ID", nullable = false)
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "TAG_ID", referencedColumnName = "TAG_ID")
    @ManyToOne
    private Tag tags;

    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE")
    @ManyToOne(cascade = CascadeType.ALL)
    private Country country;

    @ChangesInspection(type = InspectionType.CASCADE)
    @JoinColumn(name = "SITE_RATE_ID", referencedColumnName = "SITE_RATE_ID")
    @ManyToOne
    private SiteRate siteRate;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "tagPricing", fetch = FetchType.LAZY)
    private Set<SiteRate> allSiteRates = new HashSet<>(1);

    @Column(name = "CCG_RATE_TYPE", unique = false, nullable = true, insertable = true, updatable = true)
    private String ccgRateType;

    @Column(name = "CCG_TYPE", unique = false, nullable = true, insertable = true, updatable = true)
    private String ccgType;

    /**
     * Creates a new instance of TagPricing
     */
    public TagPricing() {
    }

    /**
     * Creates a new instance of TagPricing with the specified values.
     * @param tagPricingId the tagPricingId of the TagPricing
     */
    public TagPricing(Long id) {
        this.id = id;
    }

    /**
     * Creates a new instance of TagPricing with the specified values.
     * @param tagPricingId the tagPricingId of the TagPricing
     * @param tagId the tagId of the TagPricing
     */
    public TagPricing(Long id, Tag tags) {
        this.id = id;
        this.tags = tags;
    }

    /**
     * Gets the tagPricingId of this TagPricing.
     * @return the tagPricingId
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the tagPricingId of this TagPricing to the specified value.
     *
     * @param tagPricingId the new tagPricingId
     */
    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    /**
     * , Gets the tagId of this TagPricing.
     * 
     * @return the tagId
     */
    public Tag getTags() {
        return this.tags;
    }

    /**
     * Sets the tagId of this TagPricing to the specified value.
     *
     * @param tagId the new tagId
     */
    public void setTags(Tag tag) {
        this.tags = tag;
        this.registerChange("tags");
    }

    /**
     * Gets the countryCode of this TagPricing.
     * @return the countryCode
     */
    public Country getCountry() {
        return this.country;
    }

    /**
     * Sets the countryCode of this TagPricing to the specified value.
     *
     * @param countryCode the new countryCode
     */
    public void setCountry(Country country) {
        this.country = country;
        this.registerChange("country");
    }

    /**
     * Gets the siteRateId of this TagPricing.
     * @return the siteRateId
     */
    public SiteRate getSiteRate() {
        return this.siteRate;
    }

    /**
     * Sets the siteRateId of this TagPricing to the specified value.
     *
     * @param siteRateId the new siteRateId
     */
    public void setSiteRate(SiteRate siteRate) {
        this.siteRate = siteRate;
        this.registerChange("siteRate");
    }

    /**
     * Returns a hash code value for the object. This implementation computes a
     * hash code value based on the id fields in this object.
     * 
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int result = (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + (getTags() != null && getTags().getId() != null ? getTags().getId().hashCode() : 0);
        result = 31 * result + (getCountry() != null ? getCountry().hashCode() : 0);
        result = 31 * result + (getSiteRate() != null ? getSiteRate().hashCode() : 0);
        return result;
    }

    /**
     * Determines whether another object is equal to this TagPricing. The result
     * is <code>true</code> if and only if the argument is not null and is a
     * TagPricing object that has the same id field values as this object.
     * 
     * @param object
     *            the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof TagPricing)) {
            return false;
        }

        TagPricing other = (TagPricing) object;
        if (!ObjectUtils.equals(this.getId(), other.getId())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getCountry(), other.getCountry())) {
            return false;
        }

        Long tagId = this.getTags() == null ? null : this.getTags().getId();
        Long otherTagId = other.getTags() == null ? null : other.getTags().getId();

        if (!ObjectUtils.equals(tagId, otherTagId)) {
            return false;
        }

        if (!ObjectUtils.equals(this.getSiteRate(), other.getSiteRate())) {
            return false;
        }

        return true;
    }

    /**
     * Setters and getters for ccgType and ccgRateType. Pls refer to OUI-21975
     * 
     */
    public RateType getCcgRateType() {
        return ccgRateType == null ? null : RateType.valueOf(ccgRateType);
    }

    public void setCcgRateType(RateType ccgRateType) {
        this.ccgRateType = ccgRateType == null ? null : ccgRateType.toString();
    }

    public CCGType getCcgType() {
        return ccgType == null ? null : CCGType.valueOf(ccgType.toCharArray()[0]);
    }

    public void setCcgType(CCGType ccgType) {
        this.ccgType = ccgType == null ? null : Character.toString(ccgType.getLetter());
    }

    public Set<SiteRate> getAllSiteRates() {
        return allSiteRates;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "com.foros.model.site.TagPricing[id=" + id == null ? "null" : id + ", country= " + country
                + ", ccgType= " + ccgType
                + ", ccgRateType= " + ccgRateType + "]";
    }

    @Override
    public Status getParentStatus() {
        return Status.ACTIVE;
    }

    public boolean isDefault() {
        return country == null && ccgRateType == null && ccgType == null;
    }
}
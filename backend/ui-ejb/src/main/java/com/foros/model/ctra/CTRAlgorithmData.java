package com.foros.model.ctra;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.CTRAlgorithmDataEntityChange;
import com.foros.model.Country;
import com.foros.model.VersionEntityBase;
import com.foros.util.NumberUtil;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.FractionDigitsConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "CTRALGORITHM")
@Audit(nodeFactory = CTRAlgorithmDataEntityChange.Factory.class)
public class CTRAlgorithmData extends VersionEntityBase implements Serializable {

    @Id
    @RequiredConstraint
    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false)
    @OneToOne
    @ChangesInspection(type = InspectionType.NONE)
    private Country country;

    @Column(name = "CLICKS_INTERVAL1_DAYS")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer clicksInterval1Days;

    @Column(name = "CLICKS_INTERVAL1_WEIGHT")
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "9999999")
    @FractionDigitsConstraint(5)
    private BigDecimal clicksInterval1Weight;

    @Column(name = "CLICKS_INTERVAL2_DAYS")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer clicksInterval2Days;

    @Column(name = "CLICKS_INTERVAL2_WEIGHT")
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "9999999")
    @FractionDigitsConstraint(5)
    private BigDecimal clicksInterval2Weight;

    @Column(name = "CLICKS_INTERVAL3_WEIGHT")
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "9999999")
    @FractionDigitsConstraint(5)
    private BigDecimal clicksInterval3Weight;

    @Column(name = "IMPS_INTERVAL1_DAYS")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer impsInterval1Days;

    @Column(name = "IMPS_INTERVAL1_WEIGHT")
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "9999999")
    @FractionDigitsConstraint(5)
    private BigDecimal impsInterval1Weight;

    @Column(name = "IMPS_INTERVAL2_DAYS")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer impsInterval2Days;

    @Column(name = "IMPS_INTERVAL2_WEIGHT")
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "9999999")
    @FractionDigitsConstraint(5)
    private BigDecimal impsInterval2Weight;

    @Column(name = "IMPS_INTERVAL3_WEIGHT")
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "9999999")
    @FractionDigitsConstraint(5)
    private BigDecimal impsInterval3Weight;

    @Column(name = "PUB_CTR_DEFAULT")
    private BigDecimal pubCTRDefault;

    @Column(name = "SYS_CTR_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer sysCTRLevel;

    @Column(name = "PUB_CTR_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer pubCTRLevel;

    @Column(name = "SITE_CTR_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer siteCTRLevel;

    @Column(name = "TAG_CTR_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer tagCTRLevel;

    @Column(name = "KWTG_CTR_DEFAULT")
    private BigDecimal kwtgCTRDefault;

    @Column(name = "SYS_KWTG_CTR_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer sysKwtgCTRLevel;

    @Column(name = "KEYWORD_CTR_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer keywordCTRLevel;

    @Column(name = "CCGKEYWORD_KW_CTR_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer ccgkeywordKwCTRLevel;

    @Column(name = "CCGKEYWORD_TG_CTR_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer ccgkeywordTgCTRLevel;

    @Column(name = "TOW_RAW")
    private BigDecimal towRaw;

    @Column(name = "SYS_TOW_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer sysTOWLevel;

    @Column(name = "CAMPAIGN_TOW_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer campaignTOWLevel;

    @Column(name = "TG_TOW_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer tgTOWLevel;

    @Column(name = "KEYWORD_TOW_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer keywordTOWLevel;

    @Column(name = "CCGKEYWORD_KW_TOW_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer ccgkeywordKwTOWLevel;

    @Column(name = "CCGKEYWORD_TG_TOW_LEVEL")
    @RequiredConstraint
    @RangeConstraint(min = "0")
    private Integer ccgkeywordTgTOWLevel;

    @Column(name = "CPC_RANDOM_IMPS")
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "100000")
    private Integer cpcRandomImps;

    @Column(name = "CPA_RANDOM_IMPS")
    @RequiredConstraint
    @RangeConstraint(min = "0", max = "100000")
    private Integer cpaRandomImps;

    @OneToMany(mappedBy = "algorithmData", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private Set<CTRAlgorithmAdvertiserExclusion> advertiserExclusions = new LinkedHashSet<CTRAlgorithmAdvertiserExclusion>();

    @OneToMany(mappedBy = "algorithmData", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private Set<CTRAlgorithmCampaignExclusion> campaignExclusions = new LinkedHashSet<CTRAlgorithmCampaignExclusion>();

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        registerChange("countryCode");
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        registerChange("country");
        this.country = country;
    }

    public Integer getClicksInterval1Days() {
        return clicksInterval1Days;
    }

    public void setClicksInterval1Days(Integer clicksInterval1Days) {
        this.clicksInterval1Days = clicksInterval1Days;
        registerChange("clicksInterval1Days");
    }

    public BigDecimal getClicksInterval1Weight() {
        return clicksInterval1Weight;
    }

    public void setClicksInterval1Weight(BigDecimal clicksInterval1Weight) {
        this.clicksInterval1Weight = clicksInterval1Weight;
        registerChange("clicksInterval1Weight");
    }

    public Integer getClicksInterval2Days() {
        return clicksInterval2Days;
    }

    public void setClicksInterval2Days(Integer clicksInterval2Days) {
        this.clicksInterval2Days = clicksInterval2Days;
        registerChange("clicksInterval2Days");
    }

    public BigDecimal getClicksInterval2Weight() {
        return clicksInterval2Weight;
    }

    public void setClicksInterval2Weight(BigDecimal clicksInterval2Weight) {
        this.clicksInterval2Weight = clicksInterval2Weight;
        registerChange("clicksInterval2Weight");
    }

    public BigDecimal getClicksInterval3Weight() {
        return clicksInterval3Weight;
    }

    public void setClicksInterval3Weight(BigDecimal clicksInterval3Weight) {
        this.clicksInterval3Weight = clicksInterval3Weight;
        registerChange("clicksInterval3Weight");
    }

    public Integer getImpsInterval1Days() {
        return impsInterval1Days;
    }

    public void setImpsInterval1Days(Integer impsInterval1Days) {
        this.impsInterval1Days = impsInterval1Days;
        registerChange("impsInterval1Days");
    }

    public BigDecimal getImpsInterval1Weight() {
        return impsInterval1Weight;
    }

    public void setImpsInterval1Weight(BigDecimal impsInterval1Weight) {
        this.impsInterval1Weight = impsInterval1Weight;
        registerChange("impsInterval1Weight");
    }

    public Integer getImpsInterval2Days() {
        return impsInterval2Days;
    }

    public void setImpsInterval2Days(Integer impsInterval2Days) {
        this.impsInterval2Days = impsInterval2Days;
        registerChange("impsInterval2Days");
    }

    public BigDecimal getImpsInterval2Weight() {
        return impsInterval2Weight;
    }

    public void setImpsInterval2Weight(BigDecimal impsInterval2Weight) {
        this.impsInterval2Weight = impsInterval2Weight;
        registerChange("impsInterval2Weight");
    }

    public BigDecimal getImpsInterval3Weight() {
        return impsInterval3Weight;
    }

    public void setImpsInterval3Weight(BigDecimal impsInterval3Weight) {
        this.impsInterval3Weight = impsInterval3Weight;
        registerChange("impsInterval3Weight");
    }

    @RequiredConstraint
    @RangeConstraint(min = "0.01", max = "100")
    @FractionDigitsConstraint(2)
    public BigDecimal getPubCTRDefaultPercent() {
        return NumberUtil.toPercents(getPubCTRDefault());
    }

    public void setPubCTRDefaultPercent(BigDecimal pubCTRDefault) {
        setPubCTRDefault(NumberUtil.fromPercents(pubCTRDefault));
    }

    public BigDecimal getPubCTRDefault() {
        return pubCTRDefault;
    }

    public void setPubCTRDefault(BigDecimal pubCTRDefault) {
        this.pubCTRDefault = pubCTRDefault;
        registerChange("pubCTRDefault");
    }

    public Integer getSysCTRLevel() {
        return sysCTRLevel;
    }

    public void setSysCTRLevel(Integer sysCTRLevel) {
        this.sysCTRLevel = sysCTRLevel;
        registerChange("sysCTRLevel");
    }

    public Integer getPubCTRLevel() {
        return pubCTRLevel;
    }

    public void setPubCTRLevel(Integer pubCTRLevel) {
        this.pubCTRLevel = pubCTRLevel;
        registerChange("pubCTRLevel");
    }

    public Integer getSiteCTRLevel() {
        return siteCTRLevel;
    }

    public void setSiteCTRLevel(Integer siteCTRLevel) {
        this.siteCTRLevel = siteCTRLevel;
        registerChange("siteCTRLevel");
    }

    public Integer getTagCTRLevel() {
        return tagCTRLevel;
    }

    public void setTagCTRLevel(Integer tagCTRLevel) {
        this.tagCTRLevel = tagCTRLevel;
        registerChange("tagCTRLevel");
    }

    @RequiredConstraint
    @RangeConstraint(min = "0.01", max = "100")
    @FractionDigitsConstraint(2)
    public BigDecimal getKwtgCTRDefaultPercent() {
        return NumberUtil.toPercents(getKwtgCTRDefault());
    }

    public void setKwtgCTRDefaultPercent(BigDecimal kwtgCTRDefault) {
        setKwtgCTRDefault(NumberUtil.fromPercents(kwtgCTRDefault));
    }

    public BigDecimal getKwtgCTRDefault() {
        return kwtgCTRDefault;
    }

    public void setKwtgCTRDefault(BigDecimal kwtgCTRDefault) {
        this.kwtgCTRDefault = kwtgCTRDefault;
        registerChange("kwtgCTRDefault");
    }

    public Integer getSysKwtgCTRLevel() {
        return sysKwtgCTRLevel;
    }

    public void setSysKwtgCTRLevel(Integer sysKwtgCTRLevel) {
        this.sysKwtgCTRLevel = sysKwtgCTRLevel;
        registerChange("sysKwtgCTRLevel");
    }

    public Integer getKeywordCTRLevel() {
        return keywordCTRLevel;
    }

    public void setKeywordCTRLevel(Integer keywordCTRLevel) {
        this.keywordCTRLevel = keywordCTRLevel;
        registerChange("keywordCTRLevel");
    }

    public Integer getCcgkeywordKwCTRLevel() {
        return ccgkeywordKwCTRLevel;
    }

    public void setCcgkeywordKwCTRLevel(Integer ccgkeywordKwCTRLevel) {
        this.ccgkeywordKwCTRLevel = ccgkeywordKwCTRLevel;
        registerChange("ccgkeywordKwCTRLevel");
    }

    public Integer getCcgkeywordTgCTRLevel() {
        return ccgkeywordTgCTRLevel;
    }

    public void setCcgkeywordTgCTRLevel(Integer ccgkeywordTgCTRLevel) {
        this.ccgkeywordTgCTRLevel = ccgkeywordTgCTRLevel;
        registerChange("ccgkeywordTgCTRLevel");
    }

    @RequiredConstraint
    @RangeConstraint(min = "0.01", max = "100")
    @FractionDigitsConstraint(2)
    public BigDecimal getTowRawPercent() {
        return NumberUtil.toPercents(getTowRaw());
    }

    public void setTowRawPercent(BigDecimal towRaw) {
        setTowRaw(NumberUtil.fromPercents(towRaw));
    }

    public BigDecimal getTowRaw() {
        return towRaw;
    }

    public void setTowRaw(BigDecimal towRaw) {
        this.towRaw = towRaw;
        registerChange("towRaw");
    }

    public Integer getSysTOWLevel() {
        return sysTOWLevel;
    }

    public void setSysTOWLevel(Integer sysTOWLevel) {
        this.sysTOWLevel = sysTOWLevel;
        registerChange("sysTOWLevel");
    }

    public Integer getCampaignTOWLevel() {
        return campaignTOWLevel;
    }

    public void setCampaignTOWLevel(Integer campaignTOWLevel) {
        this.campaignTOWLevel = campaignTOWLevel;
        registerChange("campaignTOWLevel");
    }

    public Integer getTgTOWLevel() {
        return tgTOWLevel;
    }

    public void setTgTOWLevel(Integer tgTOWLevel) {
        this.tgTOWLevel = tgTOWLevel;
        registerChange("tgTOWLevel");
    }

    public Integer getKeywordTOWLevel() {
        return keywordTOWLevel;
    }

    public void setKeywordTOWLevel(Integer keywordTOWLevel) {
        this.keywordTOWLevel = keywordTOWLevel;
        registerChange("keywordTOWLevel");
    }

    public Integer getCcgkeywordKwTOWLevel() {
        return ccgkeywordKwTOWLevel;
    }

    public void setCcgkeywordKwTOWLevel(Integer ccgkeywordKwTOWLevel) {
        this.ccgkeywordKwTOWLevel = ccgkeywordKwTOWLevel;
        registerChange("ccgkeywordKwTOWLevel");
    }

    public Integer getCcgkeywordTgTOWLevel() {
        return ccgkeywordTgTOWLevel;
    }

    public void setCcgkeywordTgTOWLevel(Integer ccgkeywordTgTOWLevel) {
        this.ccgkeywordTgTOWLevel = ccgkeywordTgTOWLevel;
        registerChange("ccgkeywordTgTOWLevel");
    }

    public Set<CTRAlgorithmAdvertiserExclusion> getAdvertiserExclusions() {
        return new ChangesSupportSet<CTRAlgorithmAdvertiserExclusion>(this, "advertiserExclusions", advertiserExclusions);
    }

    public void setAdvertiserExclusions(Set<CTRAlgorithmAdvertiserExclusion> advertiserExclusions) {
        this.advertiserExclusions = advertiserExclusions;
        registerChange("advertiserExclusions");
    }

    public Set<CTRAlgorithmCampaignExclusion> getCampaignExclusions() {
        return new ChangesSupportSet<CTRAlgorithmCampaignExclusion>(this, "campaignExclusions", campaignExclusions);
    }

    public void setCampaignExclusions(Set<CTRAlgorithmCampaignExclusion> campaignExclusions) {
        this.campaignExclusions = campaignExclusions;
        registerChange("campaignExclusions");
    }

    public Integer getCpcRandomImps() {
        return cpcRandomImps;
    }

    public void setCpcRandomImps(Integer cpcRandomImps) {
        this.cpcRandomImps = cpcRandomImps;
        registerChange("cpcRandomImps");
    }

    public Integer getCpaRandomImps() {
        return cpaRandomImps;
    }

    public void setCpaRandomImps(Integer cpaRandomImps) {
        this.cpaRandomImps = cpaRandomImps;
        registerChange("cpaRandomImps");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CTRAlgorithmData that = (CTRAlgorithmData) o;

        if (!countryCode.equals(that.countryCode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return countryCode.hashCode();
    }
}

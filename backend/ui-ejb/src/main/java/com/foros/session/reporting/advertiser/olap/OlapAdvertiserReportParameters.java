package com.foros.session.reporting.advertiser.olap;

import com.foros.jaxb.adapters.AdvertiserReportColumnsAdapter;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.RequiredConstraint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "advertiserReportParameters")
@XmlType(propOrder = {
        "reportType",
        "unitOfTime",
        "costAndRates",
        "columns",
        "addSubtotals",
        "accountId",
        "advertiserIds",
        "campaignIds",
        "ccgIds",
        "campaignCreativeIds",
        "keyword"
})
@XmlSeeAlso({AdvertiserReportColumnsAdapter.XCollection.class})
public class OlapAdvertiserReportParameters extends DatedReportParameters {

    @RequiredConstraint
    private OlapDetailLevel reportType;

    private UnitOfTime unitOfTime;

    private CostAndRates costAndRates;

    private boolean splitWalledGardenStatistics = false;

    private Set<String> columns = new HashSet<String>();

    private boolean addSubtotals = false;

    @RequiredConstraint
    private Long accountId;

    private List<Long> advertiserIds = new ArrayList<Long>();
    private List<Long> campaignIds = new ArrayList<Long>();
    private List<Long> ccgIds = new ArrayList<Long>();
    private List<Long> campaignCreativeIds = new ArrayList<Long>();

    private String keyword;

    public OlapDetailLevel getReportType() {
        return reportType;
    }

    public void setReportType(OlapDetailLevel reportType) {
        this.reportType = reportType;
    }

    public UnitOfTime getUnitOfTime() {
        return unitOfTime;
    }

    public void setUnitOfTime(UnitOfTime unitOfTime) {
        this.unitOfTime = unitOfTime;
    }

    public CostAndRates getCostAndRates() {
        return costAndRates;
    }

    public void setCostAndRates(CostAndRates costAndRates) {
        this.costAndRates = costAndRates;
    }

    public Boolean isAddSubtotals() {
        return addSubtotals;
    }

    public void setAddSubtotals(Boolean addSubtotals) {
        this.addSubtotals = addSubtotals;
    }

    @XmlJavaTypeAdapter(AdvertiserReportColumnsAdapter.class)
    public Set<String> getColumns() {
        return columns;
    }

    public void setColumns(Set<String> columns) {
        this.columns = columns;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @XmlElement(name = "id")
    @XmlElementWrapper(name = "advertiserIds")
    public List<Long> getAdvertiserIds() {
        return advertiserIds;
    }

    public void setAdvertiserIds(List<Long> advertiserIds) {
        this.advertiserIds = advertiserIds;
    }

    @XmlElement(name = "id")
    @XmlElementWrapper(name = "campaignIds")
    public List<Long> getCampaignIds() {
        return campaignIds;
    }

    public void setCampaignIds(List<Long> campaignIds) {
        this.campaignIds = campaignIds;
    }

    @XmlElement(name = "id")
    @XmlElementWrapper(name = "adGroupIds")
    public List<Long> getCcgIds() {
        return ccgIds;
    }

    public void setCcgIds(List<Long> ccgIds) {
        this.ccgIds = ccgIds;
    }

    @XmlElement(name = "id")
    @XmlElementWrapper(name = "creativeLinkIds")
    public List<Long> getCampaignCreativeIds() {
        return campaignCreativeIds;
    }

    public void setCampaignCreativeIds(List<Long> campaignCreativeIds) {
        this.campaignCreativeIds = campaignCreativeIds;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @XmlTransient
    public boolean isSplitWalledGardenStatistics() {
        return splitWalledGardenStatistics;
    }

    public void setSplitWalledGardenStatistics(boolean splitWalledGardenStatistics) {
        this.splitWalledGardenStatistics = splitWalledGardenStatistics;
    }

    public static enum CostAndRates {
        NET,
        GROSS,
        BOTH
    }

    public static enum TimeZone {
        ACCOUNT,
        GMT
    }

    public static enum UnitOfTime {
        DATE(OlapAdvertiserMeta.DATE),
        WEEK_MON_SUN(OlapAdvertiserMeta.WEEK_MON_SUN),
        WEEK_SUN_SAT(OlapAdvertiserMeta.WEEK_SUN_SAT),
        MONTH(OlapAdvertiserMeta.MONTH),
        QUARTER(OlapAdvertiserMeta.QUARTER),
        YEAR(OlapAdvertiserMeta.YEAR);

        private final OlapColumn column;

        private UnitOfTime(OlapColumn column) {
            this.column = column;
        }

        public String getUnitName() {
            return column.getNameKey();
        }

        public OlapColumn getColumn() {
            return column;
        }
    }
}

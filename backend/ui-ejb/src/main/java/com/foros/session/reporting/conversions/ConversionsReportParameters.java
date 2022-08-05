package com.foros.session.reporting.conversions;

import com.foros.jaxb.adapters.ConversionsReportColumnsAdapter;
import com.foros.session.reporting.parameters.DatedReportParameters;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "conversionsReportParameters")
@XmlType(propOrder = {
        "accountId",
        "showResultsByDay",
        "columns",
        "conversionIds",
        "conversionAdvertiserIds",
        "campaignAdvertiserIds",
        "campaignIds",
        "groupIds",
        "creativeIds"
})
@XmlSeeAlso({ConversionsReportColumnsAdapter.ConversionsReportColumns.class})
public class ConversionsReportParameters extends DatedReportParameters {
    private Long accountId;
    private List<Long> campaignAdvertiserIds = new ArrayList<>();
    private boolean showResultsByDay;
    private List<Long> campaignIds = new ArrayList<>();
    private List<Long> groupIds = new ArrayList<>();
    private List<Long> creativeIds = new ArrayList<>();
    private List<Long> conversionAdvertiserIds = new ArrayList<>();
    private List<Long> conversionIds = new ArrayList<Long>();

    @NotEmpty
    private List<String> columns = new ArrayList<>();

    public boolean isShowResultsByDay() {
        return showResultsByDay;
    }

    public void setShowResultsByDay(boolean showResultsByDay) {
        this.showResultsByDay = showResultsByDay;
    }

    @XmlElement(name = "id")
    @XmlElementWrapper(name = "conversionIds")
    public List<Long> getConversionIds() {
        return conversionIds;
    }

    public void setConversionIds(List<Long> conversionIds) {
        this.conversionIds = conversionIds;
    }

    @XmlElement(name = "id")
    @XmlElementWrapper(name = "campaignAdvertiserIds")
    public List<Long> getCampaignAdvertiserIds() {
        return campaignAdvertiserIds;
    }

    public void setCampaignAdvertiserIds(List<Long> campaignAdvertiserIds) {
        this.campaignAdvertiserIds = campaignAdvertiserIds;
    }

    @XmlElement(name = "id")
    @XmlElementWrapper(name = "conversionAdvertiserIds")
    public List<Long> getConversionAdvertiserIds() {
        return conversionAdvertiserIds;
    }

    public void setConversionAdvertiserIds(List<Long> conversionAdvertiserIds) {
        this.conversionAdvertiserIds = conversionAdvertiserIds;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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
    @XmlElementWrapper(name = "groupIds")
    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    @XmlElement(name = "id")
    @XmlElementWrapper(name = "creativeIds")
    public List<Long> getCreativeIds() {
        return creativeIds;
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }

    @XmlJavaTypeAdapter(ConversionsReportColumnsAdapter.class)
    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}

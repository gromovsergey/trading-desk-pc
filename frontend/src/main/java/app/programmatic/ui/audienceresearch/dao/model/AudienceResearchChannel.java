package app.programmatic.ui.audienceresearch.dao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import app.programmatic.ui.channel.dao.model.ChannelEntity;
import app.programmatic.ui.common.model.Status;
import app.programmatic.ui.common.model.VersionEntityBase;
import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "AudienceResearchChannel")
public class AudienceResearchChannel extends VersionEntityBase<Long> {
    @Id
    @SequenceGenerator(name = "AudienceResearchChannelGen", sequenceName = "audienceresearchchannel_arc_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AudienceResearchChannelGen")
    @Column(name = "arc_id", nullable = false, updatable = false)
    private Long id;

    @JoinColumn(name = "ar_id", referencedColumnName = "ar_id", updatable = false, nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private AudienceResearch audienceResearch;

    @JoinColumn(name = "channel_id", referencedColumnName = "channel_id", updatable = false, nullable = false)
    @ManyToOne
    private ChannelEntity channel;

    @Column(name = "status", nullable = false)
    private Character status;

    @Column(name = "start_date", nullable = false)
    private Timestamp startDate;

    @Column(name = "chart_type", nullable = false)
    private Character chartType;

    @Column(name = "sort_order")
    private Long sortOrder;

    @Column(name = "yesterday_comment")
    private String yesterdayComment;

    @Column(name = "total_comment")
    private String totalComment;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public AudienceResearch getAudienceResearch() {
        return audienceResearch;
    }

    public void setAudienceResearch(AudienceResearch audienceResearch) {
        this.audienceResearch = audienceResearch;
    }

    public ChannelEntity getChannel() {
        return channel;
    }

    public void setChannel(ChannelEntity channel) {
        this.channel = channel;
    }

    public Status getStatus() {
        return Status.valueOf(status);
    }

    public void setStatus(Status status) {
        this.status = status.getLetter();
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public AudienceResearchChartType getChartType() {
        return AudienceResearchChartType.valueOf(chartType);
    }

    public void setChartType(AudienceResearchChartType chartType) {
        this.chartType = chartType.getLetter();
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getYesterdayComment() {
        return yesterdayComment;
    }

    public void setYesterdayComment(String yesterdayComment) {
        this.yesterdayComment = yesterdayComment;
    }

    public String getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(String totalComment) {
        this.totalComment = totalComment;
    }

    public Long getVersionAsLong() {
        return XmlDateTimeConverter.convertToEpochTime(getVersion());
    }

    public void setVersionAsLong(Long version) {
        setVersion(XmlDateTimeConverter.convertEpochToTimestamp(version));
    }
}

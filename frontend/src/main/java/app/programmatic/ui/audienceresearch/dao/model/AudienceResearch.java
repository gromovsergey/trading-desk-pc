package app.programmatic.ui.audienceresearch.dao.model;

import org.hibernate.annotations.Where;
import app.programmatic.ui.account.dao.model.AccountEntity;
import app.programmatic.ui.channel.dao.model.ChannelEntity;
import app.programmatic.ui.common.model.Status;
import app.programmatic.ui.common.model.VersionEntityBase;
import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "AudienceResearch")
public class AudienceResearch extends VersionEntityBase<Long> {
    @Id
    @SequenceGenerator(name = "AudienceResearchGen", sequenceName = "audienceresearch_ar_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AudienceResearchGen")
    @Column(name = "ar_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    private Character status;

    @JoinColumn(name = "target_channel_id", referencedColumnName = "channel_id", updatable = false, nullable = false)
    @ManyToOne
    private ChannelEntity targetChannel;

    @Column(name = "start_date", nullable = false)
    private Timestamp startDate;

    @OneToMany(mappedBy = "audienceResearch")
    @Where(clause = "status != 'D'")
    @OrderBy("sort_order")
    private List<AudienceResearchChannel> channels;

    @OneToMany
    @JoinTable(
            name = "aradvertiser",
            joinColumns = {@JoinColumn(name = "ar_id", referencedColumnName = "ar_id")},
            inverseJoinColumns = {@JoinColumn(name = "account_id", referencedColumnName = "account_id")}
    )
    @OrderBy("name")
    private List<AccountEntity> advertisers;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return Status.valueOf(status);
    }

    public void setStatus(Status status) {
        this.status = status.getLetter();
    }

    public ChannelEntity getTargetChannel() {
        return targetChannel;
    }

    public void setTargetChannel(ChannelEntity targetChannel) {
        this.targetChannel = targetChannel;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public List<AudienceResearchChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<AudienceResearchChannel> channels) {
        this.channels = channels;
    }

    public List<AccountEntity> getAdvertisers() {
        return advertisers;
    }

    public void setAdvertisers(List<AccountEntity> advertisers) {
        this.advertisers = advertisers;
    }

    public Long getVersionAsLong() {
        return XmlDateTimeConverter.convertToEpochTime(getVersion());
    }

    public void setVersionAsLong(Long version) {
        setVersion(XmlDateTimeConverter.convertEpochToTimestamp(version));
    }
}

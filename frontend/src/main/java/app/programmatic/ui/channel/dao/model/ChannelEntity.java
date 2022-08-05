package app.programmatic.ui.channel.dao.model;

import org.hibernate.annotations.DiscriminatorFormula;
import app.programmatic.ui.account.dao.model.AccountEntity;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.VersionEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Channel")
@DiscriminatorColumn(name = "channel_type")
@DiscriminatorFormula("case when value in ('B','E') then 1 else 2 end")
@DiscriminatorValue("1")
public class ChannelEntity extends VersionEntityBase<Long> {

    @Id
    @Column(name = "channel_id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "display_status_id", nullable = false)
    private Long displayStatusId;

    @JoinColumn(name = "account_id", referencedColumnName = "account_id", updatable = false)
    @ManyToOne
    private AccountEntity account;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChannelDisplayStatus getDisplayStatus() {
        return ChannelDisplayStatus.valueOf(displayStatusId.intValue());
    }

    public void setDisplayStatus(ChannelDisplayStatus displayStatus) {
        this.displayStatusId = displayStatus != null ? Long.valueOf(displayStatus.getId()) : null;
    }

    public MajorDisplayStatus getMajorDisplayStatus() {
        return ChannelDisplayStatus.valueOf(displayStatusId.intValue()).getMajorStatus();
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }
}
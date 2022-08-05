package app.programmatic.ui.account.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.VersionEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Account")
public class AccountEntity extends VersionEntityBase<Long> {

    @Id
    @Column(name = "account_id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "display_status_id", nullable = false)
    private Long displayStatusId;

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

    public AccountDisplayStatus getDisplayStatus() {
        return AccountDisplayStatus.valueOf(displayStatusId.intValue());
    }

    public void setDisplayStatus(AccountDisplayStatus displayStatus) {
        this.displayStatusId = displayStatus != null ? Long.valueOf(displayStatus.getId()) : null;
    }

    public MajorDisplayStatus getMajorDisplayStatus() {
        return AccountDisplayStatus.valueOf(displayStatusId.intValue()).getMajorStatus();
    }
}
package com.foros.model;

import com.foros.annotations.Audit;
import com.foros.changes.inspection.ChangeNode;
import com.foros.model.account.Account;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.model.security.User;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;


@Audit(nodeFactory = ChangeNode.NullFactory.class)
@Entity
@Table(name = "AUDITLOG")
@NamedQueries({
    @NamedQuery(name = "AuditLogRecord.getLastId", query = "SELECT MAX(al.id) FROM AuditLogRecord al")
})
public class AuditLogRecord implements Serializable, Identifiable {
    @GenericGenerator(name = "AuditLogGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator",
            parameters = {
                    @Parameter(name = "allocationSize", value = "20"),
                    @Parameter(name = "sequenceName", value = "AUDITLOG_LOG_ID_SEQ")})
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AuditLogGen")
    @Column(name = "LOG_ID", nullable = false)
    private Long id;

    @Column(name = "LOG_DATE", insertable = false, updatable = false)
    private Timestamp logDate;

    @Column(name = "OBJECT_ID")
    private Long objectId;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ACTION_DESCR")
    private String actionDescription;

    @Column(name = "ACTION_TYPE_ID")
    private Integer actionType;

    @Column(name = "OBJECT_TYPE_ID")
    private Integer objectTypeId;

    @Column(name = "SUCCESS")
    private boolean success = true;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "JOB_ID")
    private OracleJob financeJob;

    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @ManyToOne
    private User user;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "IP")
    @Type(type="com.foros.persistence.hibernate.type.InetType")
    private String IP;

    @JoinColumn(name = "OBJECT_ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", insertable = false, updatable = false)
    @ManyToOne
    private Account objectAccount;

    @Column(name="OBJECT_ACCOUNT_ID")
    private Long objectAccountId;

    public AuditLogRecord() {
    }

    public AuditLogRecord(long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getLogDate() {
        return this.logDate;
    }

    public void setLogDate(Timestamp logDate) {
        this.logDate = logDate;
    }

    public Long getObjectId() {
        return this.objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getActionDescription() {
        return this.actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public ActionType getActionType() {
        return ActionType.valueOf(actionType);
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType.getId();
    }

    public Integer getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(Integer objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    @Transient
    public ObjectType getObjectType() {
        return objectTypeId != null ? ObjectType.valueOf(this.objectTypeId) : null;
    }

    public void setObjectType(ObjectType objectType) {
        setObjectTypeId(objectType != null ? objectType.getId() : null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public OracleJob getFinanceJob() {
        return this.financeJob;
    }

    public void setFinanceJob(OracleJob financeJob) {
        this.financeJob = financeJob;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public Long getObjectAccountId() {
        return objectAccountId;
    }

    public void setObjectAccountId(Long objectAccountId) {
        this.objectAccountId = objectAccountId;
    }

    public Account getObjectAccount() {
        return objectAccount;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    @Override
    public boolean equals(Object o) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditLogRecord logRecord = (AuditLogRecord) o;

        return getId().equals(logRecord.getId());
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    @Override
    public String toString() {
        return "AuditLogRecord{" +
                "id=" + id +
                ", logDate=" + logDate +
                ", objectId=" + objectId +
                ", actionDescription='" + actionDescription + '\'' +
                ", actionType=" + actionType +
                ", objectTypeId=" + objectTypeId +
                ", isSucceed=" + success +
                ", financeJob=" + financeJob +
                ", user=" + user +
                ", userId=" + userId +
                ", IP='" + IP + '\'' +
                ", objectAccount=" + objectAccount +
                ", objectAccountId=" + objectAccountId +
                '}';
    }
}

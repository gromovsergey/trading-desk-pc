package com.foros.model;

import com.foros.annotations.Audit;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.audit.serialize.serializer.primitive.ApproveStatusAuditSerializer;
import com.foros.jaxb.adapters.DateTimeXmlAdapter;
import com.foros.jaxb.adapters.UserLinkXmlAdapter;
import com.foros.model.security.User;
import com.foros.util.xml.QADescription;
import com.foros.util.xml.QADescriptionHelper;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@MappedSuperclass
@XmlType(propOrder = {
        "qaStatus",
        "qaDescription",
        "qaDate",
        "qaUser"})
public abstract class ApprovableEntity extends DisplayStatusEntityBase implements Approvable, Identifiable {
    @Audit(serializer = ApproveStatusAuditSerializer.class)
    @Column(name = "QA_STATUS", nullable = false)
    private char qaStatus = 'H';
    

    @JoinColumn(name = "QA_USER_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private User qaUser;


    @Column(name = "QA_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Date qaDate;

    @Column(name = "QA_DESCRIPTION")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private String qaDescription;

    @Override
    public ApproveStatus getQaStatus() {
        return ApproveStatus.valueOf(qaStatus);
    }

    public void setQaStatus(ApproveStatus qaStatus) {
        this.qaStatus = qaStatus.getLetter();
        this.registerChange("qaStatus");
    }

    @Override
    @XmlJavaTypeAdapter(UserLinkXmlAdapter.class)
    public User getQaUser() {
        return qaUser;
    }

    public void setQaUser(User qaUser) {
        this.qaUser = qaUser;
        this.registerChange("qaUser");
    }

    @Override
    @XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    public Date getQaDate() {
        return qaDate;
    }

    public void setQaDate(Date qaDate) {
        this.qaDate = qaDate;
        this.registerChange("qaDate");
    }

    public String getQaDescription() {
        return qaDescription;
    }

    public void setQaDescription(String qaDescription) {
        this.qaDescription = qaDescription;
        this.registerChange("qaDescription");
    }

    @XmlTransient
    public QADescription getQaDescriptionObject() {
        return QADescriptionHelper.fromXML(getQaDescription());
    }

    public void setQaDescriptionObject(QADescription qaDescription) {
        setQaDescription(QADescriptionHelper.toXML(qaDescription));
    }
}

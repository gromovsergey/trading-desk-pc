package com.foros.model.security;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.primitive.HiddenPropertyAuditSerializer;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;

import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TemporalType;
import java.util.Date;
import java.io.Serializable;
import javax.xml.bind.DatatypeConverter;

@Entity
@Table(name = "USERCREDENTIALS")
@NamedQueries({
        @NamedQuery(name = "UserCredential.findByEmail", query = "SELECT uc FROM UserCredential uc WHERE  uc.email = :email"),
        @NamedQuery(name = "UserCredential.findByEmailAndPassword", query = "SELECT uc FROM UserCredential uc WHERE  uc.email = :email and password = :password"),
        @NamedQuery(name = "UserCredential.findByToken", query = "SELECT uc FROM UserCredential uc WHERE  uc.rsToken = :token"),
        @NamedQuery(name = "User.findByChangePasswordUid", query = "SELECT cp.userCredential FROM ChangePasswordUid cp WHERE cp.uid = :uid")
})
public class UserCredential extends VersionEntityBase implements Serializable, Identifiable {
    @SequenceGenerator(name = "UserCredentialGen", sequenceName = "USERCREDENTIALS_USER_CREDENTIAL_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserCredentialGen")
    @Column(name = "USER_CREDENTIAL_ID", nullable = false)
    private Long id;

    @Column(name = "EMAIL")
    private String email;

    @Audit(serializer = HiddenPropertyAuditSerializer.class)
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "RS_AUTH_TOKEN")
    private String rsToken;

    @Column(name = "RS_SIGNATURE_KEY")
    private byte[] rsKey;

    @Column(name = "WRONG_ATTEMPTS")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @ChangesInspection(type = InspectionType.NONE)
    private Integer wrongAttempts;

    @Column(name = "BLOCKED_UNTIL")
    @Temporal(TemporalType.TIMESTAMP)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @ChangesInspection(type = InspectionType.NONE)
    private Date blockedUntil;

    @Column(name = "LAST_LOGIN_DATE")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @ChangesInspection(type = InspectionType.NONE)
    private Date lastLoginDate;

    @Column(name = "LAST_LOGIN_IP")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @ChangesInspection(type = InspectionType.NONE)
    private String lastLoginIP;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.registerChange("password");
    }

    public String getRsToken() {
        return rsToken;
    }

    public void setRsToken(String rsToken) {
        this.rsToken = rsToken;
        this.registerChange("rsToken");
    }

    public byte[] getRsKey() {
        return rsKey;
    }

    public String getRsKeyBase64() {
        return DatatypeConverter.printBase64Binary(rsKey);
    }

    public void setRsKey(byte[] rsKey) {
        this.rsKey = rsKey;
        this.registerChange("rsKey");
    }

    public Integer getWrongAttempts() {
        return wrongAttempts;
    }

    public void setWrongAttempts(Integer wrongAttempts) {
        this.wrongAttempts = wrongAttempts;
        this.registerChange("wrongAttempts");
    }

    public Date getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(Date blockedUntil) {
        this.blockedUntil = blockedUntil;
        this.registerChange("blockedUntil");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.registerChange("email");
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
        this.registerChange("lastLoginDate");
    }

    public String getLastLoginIP() {
        return lastLoginIP;
    }

    public void setLastLoginIP(String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
        this.registerChange("lastLoginIP");
    }

    @Override
    public String toString() {
        return "com.foros.model.security.UserCredential[id=" + getId() + "]";
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCredential)) return false;

        UserCredential user = (UserCredential) o;

        if (id == null || !id.equals(user.getId())) return false;

        return true;
    }
}

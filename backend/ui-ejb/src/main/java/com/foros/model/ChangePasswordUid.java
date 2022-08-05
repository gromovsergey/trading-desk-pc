package com.foros.model;

import com.foros.model.security.UserCredential;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "CHANGE_PASSWORD_UID")
@NamedQueries({
    @NamedQuery(name = "ChangePasswordUid.findByUserAndUid",
        query = "select cpu from ChangePasswordUid cpu where cpu.userCredential.id = :ucid and cpu.uid = :uid")
})
public class ChangePasswordUid extends VersionEntityBase implements Identifiable {
    @SequenceGenerator(name = "ChangePasswordSeq", sequenceName = "CHANGE_PASSWORD_UID_CHANGE_PASSWORD_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChangePasswordSeq")
    @Column(name = "CHANGE_PASSWORD_ID", nullable = false)
    private Long id;

    @JoinColumn(name = "USER_CREDENTIAL_ID", referencedColumnName = "USER_CREDENTIAL_ID", nullable = false)
    @ManyToOne
    private UserCredential userCredential;

    @Column(name = "CHANGING_UID", nullable = false)
    private String uid;

    public ChangePasswordUid() {
    }

    public ChangePasswordUid(UserCredential userCredential, String uid) {
        this.userCredential = userCredential;
        this.uid = uid;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public UserCredential getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(UserCredential userCredential) {
        this.userCredential = userCredential;
        this.registerChange("userCredential");
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
        this.registerChange("uid");
    }

    @Override
    public String toString() {
        return "com.foros.model.ChangePasswordUid[id=" + getId() + "]";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? new Long(this.getId()).hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChangePasswordUid)) {
            return false;
        }

        ChangePasswordUid other = (ChangePasswordUid)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }
}

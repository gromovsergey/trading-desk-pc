package com.foros.model.security;

import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ACCOUNTADDRESS")
public class AccountAddress extends VersionEntityBase implements Serializable, Identifiable {
    @SequenceGenerator(name = "AccountAddressGen", sequenceName = "ACCOUNTADDRESS_ADDRESS_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountAddressGen")
    @Column(name = "ADDRESS_ID", nullable = false)
    private Long id;

    @Column(name = "LINE1", nullable = false)
    private String line1;

    @Column(name = "LINE2")
    private String line2;

    @Column(name = "LINE3")
    private String line3;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "ZIP")
    private String zip;

    @Column(name = "PROVINCE")
    private String province;

    public AccountAddress() {
    }

    public AccountAddress(Long id) {
        this.id = id;
    }

    public AccountAddress(Long id, String line1, String city, String zip) {
        this.id = id;
        this.line1 = line1;
        this.city = city;
        this.zip = zip;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public String getLine1() {
        return this.line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
        this.registerChange("line1");
    }

    public String getLine2() {
        return this.line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
        this.registerChange("line2");
    }

    public String getLine3() {
        return this.line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
        this.registerChange("line3");
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
        this.registerChange("city");
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
        this.registerChange("state");
    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
        this.registerChange("zip");
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
        this.registerChange("province");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccountAddress)) {
            return false;
        }
        AccountAddress other = (AccountAddress)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.security.AccountAddress[id=" + getId() + "]";
    }
}

package com.foros.model;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "COUNTRYADDRESSFIELD")
public class AddressField extends EntityBase implements Comparable<AddressField>, LocalizableNameEntity {

    private static final long ENABLED_FLAG = 1;

    private static final long MANDATORY_FLAG = 2;

    @SequenceGenerator(name = "AddressFieldGen", sequenceName = "COUNTRYADDRESSFIELD_COUNTRY_ADDRESS_FIELD_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AddressFieldGen")
    @Column(name = "COUNTRY_ADDRESS_FIELD_ID")
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName="COUNTRY_CODE",nullable=false)
    @ManyToOne
    private Country country;

    @Column(name = "FIELD_NAME")
    private String OFFieldName;

    @Column(name = "ORDER_NUMBER")
    private int orderNumber;

    @Column(name = "FLAGS")
    private long flags;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "defaultName", column = @Column(name = "NAME")),
        @AttributeOverride(name = "resourceKey", column = @Column(name = "RESOURCE_KEY"))
    })
    //@Audit(nodeFactory = EmbeddedChange.Factory.class) // Currently, equals is defined, so no need in special treatment
    private LocalizableName name = new LocalizableName();

    public AddressField() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
        this.registerChange("country");
    }

    public String getOFFieldName() {
        return OFFieldName;
    }

    public void setOFFieldName(String fieldName) {
        this.OFFieldName = fieldName;
        this.registerChange("OFFieldName");
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
        this.registerChange("orderNumber");
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
        this.registerChange("flags");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AddressField other = (AddressField) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public boolean isEnabled() {
        return (flags & ENABLED_FLAG) != 0;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.setFlags(this.getFlags() | ENABLED_FLAG);
        } else {
            this.setFlags(this.getFlags() & ~ENABLED_FLAG);
        }
    }

    public boolean isMandatory() {
        return (flags & MANDATORY_FLAG) != 0;
    }

    public void setMandatory(boolean mandatory) {
        if (mandatory) {
            this.setFlags(this.getFlags() | MANDATORY_FLAG);
        } else {
            this.setFlags(this.getFlags() & ~MANDATORY_FLAG);
        }
    }
    
    /**
     * Gets the name of this CreativeTemplate.
     * @return the name
     */
    public LocalizableName getName() {
        return this.name;
    }

    /**
     * Sets the name of this CreativeTemplate to the specified value.
     *
     * @param name the new name
     */
    public void setName(LocalizableName name) {
        this.name = name;
        this.registerChange("name");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append("[id=").append(getId()).append(",");
        sb.append("OFFieldName=").append(getOFFieldName()).append(",");
        sb.append("flags=").append(getFlags()).append(",");
        sb.append("enabled=").append(isEnabled()).append(",");
        sb.append("mandatory=").append(isMandatory()).append(",");
        sb.append("orderNumber=").append(getOrderNumber()).append(",");
        sb.append("localizableName=").append(getName()).append(",");        
        return sb.toString();
    }

    public int compareTo(AddressField that) {
        return this.getOrderNumber() - that.getOrderNumber();
    }

    
}

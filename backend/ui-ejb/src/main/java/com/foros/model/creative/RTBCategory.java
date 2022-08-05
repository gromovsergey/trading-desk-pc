package com.foros.model.creative;

import com.foros.model.Identifiable;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "RTBCATEGORY")
public class RTBCategory implements Identifiable, Serializable {
    @Id
    @GeneratedValue(generator = "RTBCategory")
    @GenericGenerator(name = "RTBCategory", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "RTBCATEGORY_RTB_CATEGORY_ID_SEQ"),
            @Parameter(name = "allocationSize", value = "20")
    })
    @IdConstraint
    @Column(name = "RTB_CATEGORY_ID", nullable = false)
    private Long id;

    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "CREATIVE_CATEGORY_ID", referencedColumnName = "CREATIVE_CATEGORY_ID", updatable = false)
    @ManyToOne
    private CreativeCategory creativeCategory;

    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "RTB_ID", referencedColumnName = "RTB_ID", updatable = false)
    @ManyToOne
    private RTBConnector rtbConnector;

    @RequiredConstraint
    @StringSizeConstraint(size = 50)
    @Column(name = "RTB_CATEGORY_KEY")
    private String name;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public RTBConnector getRtbConnector() {
        return rtbConnector;
    }

    public void setRtbConnector(RTBConnector rtbConnector) {
        this.rtbConnector = rtbConnector;
    }

    public CreativeCategory getCreativeCategory() {
        return creativeCategory;
    }

    public void setCreativeCategory(CreativeCategory creativeCategory) {
        this.creativeCategory = creativeCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

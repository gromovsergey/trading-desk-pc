package com.foros.model.channel;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.util.HashUtil;

import com.foros.util.changes.ChangesSupportList;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

@Entity
@Table(name = "BEHAVIORALPARAMETERSLIST")
@NamedQueries({
    @NamedQuery(name = "BehavioralParametersList.findAll", query = "SELECT bpl FROM BehavioralParametersList bpl ORDER BY upper(bpl.name) ")
})
public class BehavioralParametersList extends VersionEntityBase implements Identifiable {
    @SequenceGenerator(name = "BehavioralParametersListGen", sequenceName = "BEHAVIORALPARAMETERSLIST_BEHAV_PARAMS_LIST_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BehavioralParametersListGen")
    @Column(name = "BEHAV_PARAMS_LIST_ID", nullable = false)
    private Long id;

    @RequiredConstraint
    @NameConstraint
    @Column(name = "NAME", nullable = false)
    private String name;

    @RequiredConstraint
    @Column(name = "THRESHOLD", nullable = true)
    @RangeConstraint(min = "1", max = "2147483647")
    private Long threshold;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "paramsList", cascade = CascadeType.ALL)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedList.class)
    private List<BehavioralParameters> behavioralParameters = new LinkedList<BehavioralParameters>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
        this.registerChange("threshold");
    }

    public List<BehavioralParameters> getBehavioralParameters() {
        return new ChangesSupportList<BehavioralParameters>(this, "behavioralParameters", behavioralParameters);
    }

    public void setBehavioralParameters(List<BehavioralParameters> behavioralParameters) {
        this.behavioralParameters = behavioralParameters;
        this.registerChange("behavioralParameters");
    }

    @Override
    public int hashCode() {
        return HashUtil.calculateHash(id, name, threshold);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BehavioralParametersList)) {
            return false;
        }

        BehavioralParametersList other = (BehavioralParametersList) obj;

        if (!ObjectUtils.equals(this.getId(), other.getId())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getName(), other.getName())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getThreshold(), other.getThreshold())) {
            return false;
        }

        return true;
    }
}

package com.foros.model.channel;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.BPTriggerTypeAdapter;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import javax.annotation.security.DenyAll;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "BEHAVIORALPARAMETERS")
@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class BehavioralParameters extends VersionEntityBase implements Serializable, Identifiable {

    @GenericGenerator(name = "BehavioralParametersGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator",
            parameters = {
                    @Parameter(name = "allocationSize", value = "20"),
                    @Parameter(name = "sequenceName", value = "BEHAVIORALPARAMETERS_BEHAV_PARAMS_ID_SEQ")})
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BehavioralParametersGen")
    @Column(name = "BEHAV_PARAMS_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID", nullable = true)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Channel channel;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "BEHAV_PARAMS_LIST_ID", referencedColumnName = "BEHAV_PARAMS_LIST_ID", nullable = true)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private BehavioralParametersList paramsList;

    @Column(name = "MINIMUM_VISITS")
    @RequiredConstraint
    @RangeConstraint(min = "1", max = "99")
    private Long minimumVisits;

    @Column(name = "TIME_FROM")
    @RangeConstraint(min = "0")
    private Long timeFrom;

    @Column(name = "TIME_TO")
    @RangeConstraint(min = "0")
    private Long timeTo;

    @Column(name = "TRIGGER_TYPE")
    @RequiredConstraint
    private Character triggerType;

    @Column(name = "WEIGHT")
    @RangeConstraint(min = "1", max = "100")
    @RequiredConstraint
    private Long weight = 1L;

    public BehavioralParameters() {
    }

    @Override
    @XmlTransient
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlTransient
    public Channel getChannel() {
        return this.channel;
    }

    @DenyAll
    public void setChannel(Channel channel) {
        this.channel = channel;
        this.registerChange("channel");
    }

    @XmlTransient
    public BehavioralParametersList getParamsList() {
        return paramsList;
    }

    public void setParamsList(BehavioralParametersList paramsList) {
        this.paramsList = paramsList;
        this.registerChange("paramsList");
    }

    public Long getMinimumVisits() {
        return minimumVisits;
    }

    public void setMinimumVisits(Long minimumVisits) {
        this.minimumVisits = minimumVisits;
        this.registerChange("minimumVisits");
    }

    public Long getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Long timeFrom) {
        this.timeFrom = timeFrom;
        this.registerChange("timeFrom");
    }

    public Long getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Long timeTo) {
        this.timeTo = timeTo;
        this.registerChange("timeTo");
    }

    public boolean isUrlTriggerType() {
        return TriggerType.URL.equalsTo(triggerType);
    }

    public boolean isPageTriggerType() {
        return TriggerType.PAGE_KEYWORD.equalsTo(triggerType);
    }

    public boolean isSearchTriggerType() {
        return TriggerType.SEARCH_KEYWORD.equalsTo(triggerType);
    }

    public boolean isUrlKeywordTriggerType() {
        return TriggerType.URL_KEYWORD.equalsTo(triggerType);
    }

    @XmlJavaTypeAdapter(BPTriggerTypeAdapter.class)
    public Character getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Character triggerType) {
        this.triggerType = triggerType;
        this.registerChange("triggerType");
    }

    @XmlTransient
    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
        this.registerChange("weight");
    }

    @Override
    public String toString() {
        return "BehavioralParameters[id=" + this.getId() + "]";
    }
}

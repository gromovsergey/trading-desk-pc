package com.foros.model.campaign;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.CampaignCreativeGroupXmlAdapter;
import com.foros.jaxb.adapters.CreativeXmlAdapter;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntityBase;
import com.foros.model.FrequencyCap;
import com.foros.model.FrequencyCapEntity;
import com.foros.model.Identifiable;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.security.OwnedStatusable;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "CAMPAIGNCREATIVE")
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED})
@XmlRootElement(name = "creativeLink")
@XmlType(propOrder = {
        "id",
        "creativeGroup",
        "creative",
        "weight",
        "frequencyCap"
})
@XmlAccessorType(XmlAccessType.NONE)
public class CampaignCreative extends DisplayStatusEntityBase implements OwnedStatusable<AdvertiserAccount>, Serializable, Identifiable, FrequencyCapEntity {
    public static final Long DEFAULT_WEIGHT = 1L;

    @Id
    @GeneratedValue(generator = "CampaignCreativeGen")
    @GenericGenerator(name = "CampaignCreativeGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator", parameters = {
                    @Parameter(name = "sequenceName", value = "CAMPAIGNCREATIVE_CC_ID_SEQ"),
                    @Parameter(name = "allocationSize", value = "20")
            }
    )
    @Column(name = "CC_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @IdConstraint
    private Long id;

    @Column(name = "WEIGHT")
    @RequiredConstraint
    @RangeConstraint(min = "1", max = "64000")
    private Long weight = DEFAULT_WEIGHT;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID", updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    @RequiredConstraint
    @HasIdConstraint
    private CampaignCreativeGroup creativeGroup;

    @JoinColumn(name = "CREATIVE_ID", referencedColumnName = "CREATIVE_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    @ChangesInspection(type = InspectionType.FIELD)
    @RequiredConstraint
    @HasIdConstraint
    private Creative creative;

    @JoinColumn(name = "FREQ_CAP_ID", referencedColumnName = "FREQ_CAP_ID")
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @CopyPolicy(strategy = CopyStrategy.DEEP)
    private FrequencyCap frequencyCap;

    @Column(name = "SET_NUMBER")
    @RequiredConstraint
    @RangeConstraint(min = "1", max = "9999999999")
    private Long setNumber = 1L;

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "campaigncreative.displaystatus.live");
    public static final DisplayStatus NOT_LIVE = new DisplayStatus(2L, DisplayStatus.Major.NOT_LIVE, "campaigncreative.displaystatus.not_live");
    public static final DisplayStatus INACTIVE = new DisplayStatus(3L, DisplayStatus.Major.INACTIVE, "campaigncreative.displaystatus.inactive");
    public static final DisplayStatus DELETED = new DisplayStatus(4L, DisplayStatus.Major.DELETED, "campaigncreative.displaystatus.deleted");

    public static final Map<Long, DisplayStatus> displayStatusMap = getDisplayStatusMap(
            LIVE,
            NOT_LIVE,
            INACTIVE,
            DELETED
    );

    public static DisplayStatus getDisplayStatus(Long id) {
        return displayStatusMap.get(id);
    }

    public static Collection<DisplayStatus> getAvailableDisplayStatuses() {
        return displayStatusMap.values();
    }

    @Override
    public DisplayStatus getDisplayStatus() {
        return displayStatusMap.get(displayStatusId);
    }

    public CampaignCreative() {
    }

    public CampaignCreative(Long id) {
        this.id = id;
    }

    @Override
    @XmlElement
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlElement
    public Long getWeight() {
        return this.weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
        this.registerChange("weight");
    }

    @XmlElement
    @XmlJavaTypeAdapter(CampaignCreativeGroupXmlAdapter.class)
    public CampaignCreativeGroup getCreativeGroup() {
        return this.creativeGroup;
    }

    public void setCreativeGroup(CampaignCreativeGroup ccg) {
        this.creativeGroup = ccg;
        this.registerChange("creativeGroup");
    }

    @XmlElement
    @XmlJavaTypeAdapter(CreativeXmlAdapter.class)
    public Creative getCreative() {
        return this.creative;
    }

    public void setCreative(Creative creative) {
        this.creative = creative;
        this.registerChange("creative");
    }

    @Override
    @XmlElement
    public FrequencyCap getFrequencyCap() {
        return this.frequencyCap;
    }

    @Override
    public void setFrequencyCap(FrequencyCap freqCap) {
        this.frequencyCap = freqCap;
        this.registerChange("frequencyCap");
    }

    @Override
    public String toString() {
        return "com.foros.model.campaign.CampaignCreative[id=" + getId() + "]";
    }

    @Override
    public AdvertiserAccount getAccount() {
        return creativeGroup.getCampaign().getAccount();
    }

    @Override
    public Status getParentStatus() {
        return creativeGroup.getInheritedStatus();
    }

    public Long getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(Long setNumber) {
        this.setNumber = setNumber;
        this.registerChange("setNumber");
    }
}

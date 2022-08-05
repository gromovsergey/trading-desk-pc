package com.foros.model.action;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.jaxb.adapters.AdvertiserAgencyAccountXmlAdapter;
import com.foros.jaxb.adapters.ConversionCategoryXmlAdapter;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntityBase;
import com.foros.model.IdNameEntity;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.security.OwnedStatusable;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.SizeConstraint;
import com.foros.validation.constraint.UrlConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "ACTION")
@NamedQueries({
    @NamedQuery(name = "Action.findNonDeletedByAccountId", query = "SELECT a FROM Action a WHERE a.account.id = :accountId and a.status <> 'D' ORDER BY a.account.name, a.name")
})
@AllowedStatuses(values = {Status.ACTIVE, Status.DELETED})
@XmlRootElement(name = "conversion")
@XmlType(propOrder = {
        "id",
        "account",
        "name",
        "conversionCategory",
        "url",
        "impWindow",
        "clickWindow",
        "value"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Action extends DisplayStatusEntityBase implements OwnedStatusable<AdvertiserAccount>, IdNameEntity, Serializable {

    public static final BigDecimal VALUE_MAX = new BigDecimal("1000000000");

    @SequenceGenerator(name = "ActionGen", sequenceName = "ACTION_ACTION_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ActionGen")
    @Column(name = "ACTION_ID", nullable = false)
    @IdConstraint
    private Long id;

    @NameConstraint
    @RequiredConstraint
    @Column(name = "NAME", nullable = false)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @RequiredConstraint
    @Column(name = "CONV_CATEGORY_ID", nullable=false)
    private ConversionCategory conversionCategory;

    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private AdvertiserAccount account;

    @UrlConstraint
    @Column(name = "URL")
    private String url;

    @RequiredConstraint
    @Column(name = "IMP_WINDOW")
    @SizeConstraint(min = 1, max = 30)
    private Integer impWindow;

    @RequiredConstraint
    @Column(name = "CLICK_WINDOW")
    @SizeConstraint(min = 1, max = 90)
    private Integer clickWindow;

    @RequiredConstraint
    @Column(name = "CUR_VALUE", precision = 10, scale = 5, nullable = false)
    private BigDecimal value = BigDecimal.ZERO;

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "action.displaystatus.live");
    public static final DisplayStatus DELETED = new DisplayStatus(2L, DisplayStatus.Major.DELETED, "action.displaystatus.deleted");
    public static final DisplayStatus LIVE_NEED_ATT = new DisplayStatus(3L, DisplayStatus.Major.LIVE_NEED_ATT, "action.displaystatus.live_na");
    public static final DisplayStatus NOT_LIVE = new DisplayStatus(4L, DisplayStatus.Major.NOT_LIVE, "action.displaystatus.not_live");

    public static Map<Long, DisplayStatus> displayStatusMap = getDisplayStatusMap(
        LIVE,
        LIVE_NEED_ATT,
        NOT_LIVE,
        DELETED
    );

    public static DisplayStatus getDisplayStatus(Long id) {
        return displayStatusMap.get(id);
    }

    @Override
    public DisplayStatus getDisplayStatus() {
        return getDisplayStatus(displayStatusId);
    }

    public Action() {
    }

    public Action(Long id) {
        this.id = id;
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

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    @XmlJavaTypeAdapter(ConversionCategoryXmlAdapter.class)
    public ConversionCategory getConversionCategory() {
        return conversionCategory;
    }

    public void setConversionCategory(ConversionCategory conversionCategory) {
        this.conversionCategory = conversionCategory;
        this.registerChange("conversionCategory");
    }

    @Override
    @XmlElement
    @XmlJavaTypeAdapter(AdvertiserAgencyAccountXmlAdapter.class)
    public AdvertiserAccount getAccount() {
        return this.account;
    }

    public void setAccount(AdvertiserAccount account) {
        this.account = account;
        this.registerChange("account");
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.registerChange("url");
    }

    public Integer getImpWindow() {
        return this.impWindow;
    }

    public void setImpWindow(Integer impWindow) {
        this.impWindow = impWindow;
        this.registerChange("impWindow");
    }

    public Integer getClickWindow() {
        return this.clickWindow;
    }

    public void setClickWindow(Integer clickWindow) {
        this.clickWindow = clickWindow;
        this.registerChange("clickWindow");
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
        if (!(object instanceof Action)) {
            return false;
        }

        Action other = (Action) object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }

    @Override
    public Status getParentStatus() {
        return account.getInheritedStatus();
    }

    @Override
    public String toString() {
        return "com.foros.model.action.Action[id=" + getId() + "]";
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
        this.registerChange("value");
    }
}

package com.foros.model.campaign;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.CampaignCreativeGroupXmlAdapter;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntityBase;
import com.foros.model.Identifiable;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.account.Account;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.model.security.OwnedStatusable;
import com.foros.util.mapper.Pair;
import com.foros.validation.constraint.ByteLengthConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.UrlConstraint;
import com.foros.validation.util.DuplicateChecker;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@XmlRootElement
@XmlType(propOrder = {
        "id",
        "originalKeyword",
        "triggerType",
        "creativeGroup",
        "maxCpcBid",
        "clickURL"
})
@Entity
@Table(name = "CCGKEYWORD")
@NamedQueries({
    @NamedQuery(name = "CCGKeyword.findByGroup", query = "SELECT c FROM CCGKeyword c WHERE c.creativeGroup.id = :groupId"),
    @NamedQuery(name = "CCGKeyword.findByOriginalKeyword", query = "SELECT c FROM CCGKeyword c WHERE c.creativeGroup.id = :groupId AND c.originalKeyword = :originalKeyword")
})
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED})
public class CCGKeyword extends StatusEntityBase implements Serializable, Identifiable, OwnedStatusable {

    public static final DuplicateChecker.IdentifierFetcher<CCGKeyword> IDENTIFIER_FETCHER = new IdentifierFetcher();
    @Id
    @GeneratedValue(generator = "CCGKeywordGen")
    @GenericGenerator(name = "CCGKeywordGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator", parameters = {
                    @Parameter(name = "sequenceName", value = "CCGKEYWORD_CCG_KEYWORD_ID_SEQ"),
                    @Parameter(name = "allocationSize", value = "20")
            }
    )
    @Column(name = "CCG_KEYWORD_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @IdConstraint
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID", updatable = false)
    @ManyToOne
    @Fetch(FetchMode.SELECT)
    private CampaignCreativeGroup creativeGroup;

    @RequiredConstraint
    @Column(name = "ORIGINAL_KEYWORD", nullable = false)
    private String originalKeyword;

    @Column(name = "MAX_CPC_BID", precision = 12, scale = 5)
    private BigDecimal maxCpcBid;

    @UrlConstraint
    @ByteLengthConstraint(length = 2048)
    @Column(name = "CLICK_URL")
    private String clickURL;

    @Column(name = "CHANNEL_ID")
    private Long channelId;

    @RequiredConstraint
    @Column(name = "TRIGGER_TYPE", nullable = false, updatable = false)
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.channel.KeywordTriggerType"),
            @Parameter(name = "identifierMethod", value = "getLetter"),
            @Parameter(name = "valueOfMethod", value = "byLetter")
    })
    private KeywordTriggerType triggerType;

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "ccgkeyword.displaystatus.live");
    public static final DisplayStatus NOT_LIVE_DECLINED = new DisplayStatus(2L, DisplayStatus.Major.NOT_LIVE, "ccgkeyword.displaystatus.not_live_declined");
    public static final DisplayStatus NOT_LIVE_PENDING = new DisplayStatus(3L, DisplayStatus.Major.NOT_LIVE, "ccgkeyword.displaystatus.not_live_pending");
    public static final DisplayStatus INACTIVE = new DisplayStatus(4L, DisplayStatus.Major.INACTIVE, "ccgkeyword.displaystatus.inactive");
    public static final DisplayStatus DELETED = new DisplayStatus(5L, DisplayStatus.Major.DELETED, "ccgkeyword.displaystatus.deleted");
    public static final DisplayStatus NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS = new DisplayStatus(6L, DisplayStatus.Major.NOT_LIVE, "ccgkeyword.displaystatus.not_live_not_enough_users","ccgkeyword.displaystatus.not_live_pending");
    public static final DisplayStatus LIVE_NEED_ATT = new DisplayStatus(7L, DisplayStatus.Major.LIVE_NEED_ATT, "ccgkeyword.displaystatus.live_na");

    static private Map<Long, DisplayStatus> displayStatusMap = DisplayStatusEntityBase.getDisplayStatusMap(
            LIVE,
            NOT_LIVE_DECLINED,
            NOT_LIVE_PENDING,
            INACTIVE,
            DELETED,
            NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS
    );


    public CCGKeyword() {
    }

    /**
     * Gets the id of this CCGKeyword.
     *
     * @return the ccgKeywordId
     */
    @Override
    public Long getId() {
        return this.id;
    }

    static public DisplayStatus getDisplayStatus(Long id) {
        return displayStatusMap.get(id);
    }

    /**
     * Sets the id of this CCGKeyword to the specified value.
     *
     * @param id the new ccgKeywordId
     */
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    /**
     * Retirns keyword price
     * @return keyword price
     */
    public BigDecimal getMaxCpcBid() {
        return maxCpcBid;
    }

    /**
     * Sets keyword price for none-negative keywords
     *
     * @param maxCpcBid keyword price
     */
    public void setMaxCpcBid(BigDecimal maxCpcBid) {
        this.maxCpcBid = maxCpcBid;
        this.registerChange("maxCpcBid");
    }

    /**
     *
     * Returns original (not normilized) keyword
     * @return original keyword entered by user
     */
    public String getOriginalKeyword() {
        return originalKeyword;
    }

    /**
     * Sets original (not normilized) keyword
     *
     * @param originalKeyword original keyword
     */
    public void setOriginalKeyword(String originalKeyword) {
        this.originalKeyword = originalKeyword;
        this.registerChange("originalKeyword");
    }

    @XmlElement
    @XmlJavaTypeAdapter(CampaignCreativeGroupXmlAdapter.class)
    public CampaignCreativeGroup getCreativeGroup() {
        return creativeGroup;
    }

    public void setCreativeGroup(CampaignCreativeGroup campaignCreativeGroup) {
        this.creativeGroup = campaignCreativeGroup;
        this.registerChange("creativeGroup");
    }

    public void setClickURL(String clickURL) {
        this.clickURL = clickURL;
        this.registerChange("clickURL");
    }

    public String getClickURL() {
        return clickURL;
    }

    @XmlTransient
    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
        this.registerChange("channelId");
    }

    public KeywordTriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(KeywordTriggerType triggetType) {
        this.triggerType = triggetType;
        this.registerChange("triggetType");
    }

    @Override
    public String toString() {
        return "CCGKeyword{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", originalKeyword='" + getOriginalKeyword() + '\'' +
                '}';
    }

    public boolean isNegative() {
        return isNegativeKeyword(getOriginalKeyword());
    }

    public static boolean isNegativeKeyword(String keyword) {
        return keyword != null && keyword.trim().startsWith("-");
    }

    @Override
    public Status getParentStatus() {
        return creativeGroup.getInheritedStatus();
    }

    @Override
    public Account getAccount() {
        return creativeGroup.getAccount();
    }

    public static class IdentifierFetcher implements DuplicateChecker.IdentifierFetcher<CCGKeyword> {
        @Override
        public Pair<String, KeywordTriggerType> fetch(CCGKeyword keyword) {
            return new Pair<String, KeywordTriggerType>(keyword.getOriginalKeyword(), keyword.getTriggerType());
        }
    }
}

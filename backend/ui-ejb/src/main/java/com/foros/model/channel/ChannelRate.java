package com.foros.model.channel;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.DateTimeXmlAdapter;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.model.campaign.RateType;
import com.foros.model.currency.Currency;
import com.foros.session.BusinessException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "CHANNELRATE")
public class ChannelRate extends EntityBase implements Identifiable, Serializable {
    private static final List<RateType> allowedTypes = Collections.unmodifiableList(Arrays.asList(RateType.CPM, RateType.CPC));

    @SequenceGenerator(name = "ChannelRateGen", sequenceName = "CHANNELRATE_CHANNEL_RATE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChannelRateGen")
    @Column(name = "CHANNEL_RATE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID", nullable = false)
    @ManyToOne
    private Channel channel;

    @Column(name = "CPM")
    private BigDecimal cpm;

    @Column(name = "CPC")
    private BigDecimal cpc;

    @Column(name = "RATE_TYPE")
    @Enumerated(EnumType.STRING)
    private RateType rateType;

    @Column(name = "EFFECTIVE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveDate;

    @JoinColumn(name = "CURRENCY_ID", referencedColumnName = "CURRENCY_ID", nullable = false)
    @ManyToOne
    private Currency currency;

    public ChannelRate() {
    }

    public ChannelRate(Long id) {
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

    @XmlTransient
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        this.registerChange("channel");
    }

    public BigDecimal getCpm() {
        return this.cpm;
    }

    public void setCpm(BigDecimal cpm) {
        this.cpm = cpm;
        this.registerChange("cpm");
    }

    public BigDecimal getCpc() {
        return this.cpc;
    }

    public void setCpc(BigDecimal cpc) {
        this.cpc = cpc;
        this.registerChange("cpc");
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
        this.registerChange("rateType");
    }

    public BigDecimal getRate() {
        if (rateType == null) {
            return null;
        }

        switch (rateType) {
            case CPC:
                return cpc;
            case CPM:
                return cpm;
            default:
                return null;
        }
    }

    public void setRate(BigDecimal rate, RateType rateType) {
        switch (rateType) {
            case CPC:
                setCpc(rate);
                setCpm(BigDecimal.ZERO);
                break;
            case CPM:
                setCpc(BigDecimal.ZERO);
                setCpm(rate);
                break;
            default:
                throw new BusinessException("Wrong channel rate type: " + rateType);
        }
        setRateType(rateType);
    }

    public static List<RateType> getAllowedTypes() {
        return allowedTypes;
    }

    @XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
        this.registerChange("effectiveDate");
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        this.registerChange("currency");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChannelRate)) {
            return false;
        }

        ChannelRate rate = (ChannelRate) o;
        return getId() != null ? getId().equals(rate.getId()) : rate.getId() == null;
    }
}

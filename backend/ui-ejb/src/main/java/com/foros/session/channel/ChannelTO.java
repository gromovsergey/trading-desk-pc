package com.foros.session.channel;

import com.foros.model.DisplayStatus;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.security.AccountRole;
import com.foros.session.QAEntityTO;
import com.foros.util.FlagsUtil;
import com.foros.util.expression.ExpressionHelper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ChannelTO extends QAEntityTO implements Serializable {
    private long flags;
    private char channelType;
    private Long accountId;
    private AccountRole accountRole;
    private String accountName;
    private DisplayStatus accountDisplayStatus;
    private Long accountManagerId;
    private Integer reuse;
    private Long imps;
    private Date lastUse;
    private BigDecimal userCount;
    private String country;
    private ChannelVisibility visibility;
    private BigDecimal rate;
    private String rateType;
    private Long currencyId;
    private Long triggerId;
    private boolean accountTestFlag;
    private BigDecimal overlapLevel = BigDecimal.ZERO;

    public ChannelTO() {
        super();
    }

    public ChannelTO(Long id, String name, char status, char qaStatus, Long displayStatusId, String channelType) {
        super(id, name, status, qaStatus, Channel.getDisplayStatus(displayStatusId));
        this.channelType = channelType.charAt(0);
    }

    public ChannelTO(Long id, String name, char status, char qaStatus, Long displayStatusId, String country, ChannelVisibility visibility, String channelType) {
        super(id, name, status, qaStatus, Channel.getDisplayStatus(displayStatusId));
        this.visibility = visibility;
        this.country = country;
        this.channelType = channelType.charAt(0);
    }

    public ChannelTO(Long id, String name, long flags, char status, char qaStatus,
            Long accountId, AccountRole accountRole, String accountName, Long accountManagerId, Long displayStatusId) {
        super(id, name, status, qaStatus, Channel.getDisplayStatus(displayStatusId));

        this.flags = flags;
        this.accountId = accountId;
        this.accountRole = accountRole;
        this.accountName = accountName;
        this.accountManagerId = accountManagerId;
    }

    public ChannelTO(Long id, String name, long flags, char status, char qaStatus, Long displayStatusId,
                     Long accountId, AccountRole accountRole, String accountName, Long accountDisplayStatusId, Long accountManagerId,
                     Long imps, BigDecimal userCount, String country) {
        super(id, name, status, qaStatus, Channel.getDisplayStatus(displayStatusId));

        this.flags = flags;
        this.accountId = accountId;
        this.accountRole = accountRole;
        this.accountName = accountName;
        this.accountManagerId = accountManagerId;
        this.imps = imps;
        this.userCount = userCount;
        this.country = country;
        this.accountDisplayStatus = Account.getDisplayStatus(accountDisplayStatusId);
    }

    public ChannelTO(Long id, String name, char status, char qaStatus, Long displayStatusId, String channelType, Long triggerId) {
        super(id, name, status, qaStatus, Channel.getDisplayStatus(displayStatusId));
        this.channelType = channelType.charAt(0);
        this.triggerId = triggerId;
    }


    public long getFlags() {
        return flags;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(AccountRole role) {
        accountRole = role;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public boolean isBehavioralChannel() {
        return channelType != Channel.CHANNEL_TYPE_EXPRESSION.charAt(0);
    }

    public String getFullName() {
        return ExpressionHelper.formatChannelName(this);
    }

    public Long getAccountManagerId() {
        return accountManagerId;
    }

    public Integer getReuse() {
        return reuse;
    }

    public void setReuse(Integer reuse) {
        this.reuse = reuse;
    }

    public Long getImps() {
        return imps;
    }

    public void setImps(Long imps) {
        this.imps = imps;
    }

    public Date getLastUse() {
        return lastUse;
    }

    public void setLastUse(Date lastUse) {
        this.lastUse = lastUse;
    }

    public BigDecimal getUserCount() {
        return userCount;
    }

    public void setUserCount(BigDecimal userCount) {
        this.userCount = userCount;
    }

    public Character getChannelType() {
        return channelType;
    }

    public void setChannelType(Character channelType) {
        this.channelType = channelType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ChannelVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ChannelVisibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "ChannelTO{" +
                "accountId=" + accountId +
                ", id=" + getId() +
                ", name=" + getName() +
                ", accountRole=" + accountRole +
                ", visibility=" + visibility +
                ", accountName='" + accountName + '\'' +
                ", accountManagerId=" + accountManagerId +
                ", channelType=" + channelType +
                ", flags=" + flags +
                ", country='" + country + '\'' +
                '}';
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public DisplayStatus getAccountDisplayStatus() {
        return accountDisplayStatus;
    }

    public void setAccountDisplayStatus(DisplayStatus accountDisplayStatus) {
        this.accountDisplayStatus = accountDisplayStatus;
    }

    public Long getTriggerId() {
        return triggerId;
    }

    public boolean isAccountTestFlag() {
        return accountTestFlag;
    }

    public void setAccountTestFlag(long accountTestFlag) {
        this.accountTestFlag = FlagsUtil.get(accountTestFlag, Account.TEST_FLAG);
    }

    public BigDecimal getOverlapLevel() {
        return overlapLevel;
    }

    public void setOverlapLevel(BigDecimal overlapLevel) {
        this.overlapLevel = overlapLevel;
    }
}

package com.foros.action.admin.triggerQA;

import com.foros.action.SearchForm;
import com.foros.model.channel.trigger.TriggerChannelType;
import com.foros.security.AccountRole;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.session.channel.triggerQA.TriggerQASearchFilter;
import com.foros.session.channel.triggerQA.TriggerQAType;
import com.foros.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class TriggersSearchForm extends SearchForm {
    private TriggerQAType triggerType = null;
    private Character approval = null;
    private String roleName = null;
    private String criteria = null;
    private ChannelVisibilityCriteria visibility = null;

    private Long ccgAccountId = null;
    private Long channelAccountId = null;
    private Long channelId = null;
    private String channelName = null;
    private Long displayStatusId = null;
    private TriggerChannelType type = TriggerChannelType.ADVERTISING;
    private String countryCode = null;

    private Long advertiserId = null;
    private Long campaignId = null;
    private Long ccgId = null;

    private TriggerQASearchFilter filterBy = TriggerQASearchFilter.ALL;

    private Long discoverAccountId = null;
    private Long discoverChannelId = null;
    private Long discoverDisplayStatusId = null;
    private Long discoverChannelListId = null;
    private String discoverChannelName = null;

    private String orderBy;

    public TriggerQAType getTriggerType() {
        return triggerType;
    }

    public Long getCcgAccountId() {
        return ccgAccountId;
    }

    public void setCcgAccountId(Long ccgAccountId) {
        this.ccgAccountId = ccgAccountId;
    }

    public Long getChannelAccountId() {
        return channelAccountId;
    }

    public void setChannelAccountId(Long channelAccountId) {
        this.channelAccountId = channelAccountId;
    }

    public void setTriggerType(TriggerQAType triggerType) {
        this.triggerType = triggerType;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public Character getApproval() {
        return approval;
    }

    public void setApproval(Character approval) {
        this.approval = approval;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Long getAccountId() {
        if (filterBy == TriggerQASearchFilter.CHANNEL) {
            return getChannelAccountId();
        } else if (filterBy == TriggerQASearchFilter.CCG) {
            return getCcgAccountId();
        } else {
            return null;
        }
    }

    public ChannelVisibilityCriteria getVisibility() {
        return visibility;
    }

    public void setVisibility(ChannelVisibilityCriteria visibility) {
        this.visibility = visibility;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getDisplayStatusId() {
        return displayStatusId;
    }

    public void setDisplayStatusId(Long displayStatusId) {
        this.displayStatusId = displayStatusId;
    }

    public Set<AccountRole> getRoles() {
        Set<AccountRole> roles;
        if (StringUtil.isPropertyEmpty(roleName)) {
            roles = null;
        } else if ("INTERNAL".equals(roleName)) {
            roles = new HashSet<AccountRole>();
            roles.add(AccountRole.INTERNAL);
        } else if ("EXTERNAL".equals(roleName)) {
            roles = new HashSet<AccountRole>();
            roles.add(AccountRole.ADVERTISER);
            roles.add(AccountRole.AGENCY);
            roles.add(AccountRole.CMP);
        } else {
            throw new IllegalArgumentException("Unsupported role filter: " + roleName);
        }
        return roles;
    }

    public TriggerChannelType getType() {
        return type;
    }

    public void setType(TriggerChannelType type) {
        this.type = type;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    public TriggerQASearchFilter getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(TriggerQASearchFilter filterBy) {
        this.filterBy = filterBy;
    }

    public Long getDiscoverAccountId() {
        return discoverAccountId;
    }

    public void setDiscoverAccountId(Long discoverAccountId) {
        this.discoverAccountId = discoverAccountId;
    }

    public Long getDiscoverChannelId() {
        return discoverChannelId;
    }

    public void setDiscoverChannelId(String discoverChannelId) {
        try {
            this.discoverChannelId = Long.valueOf(discoverChannelId);
        } catch (NumberFormatException nex) {
            this.discoverChannelId = StringUtils.isEmpty(discoverChannelId) ? null : -1L;
        }
    }

    public Long getDiscoverDisplayStatusId() {
        return discoverDisplayStatusId;
    }

    public void setDiscoverDisplayStatusId(Long discoverDisplayStatusId) {
        this.discoverDisplayStatusId = discoverDisplayStatusId;
    }

    public Long getDiscoverChannelListId() {
        return discoverChannelListId;
    }

    public void setDiscoverChannelListId(Long discoverChannelListId) {
        this.discoverChannelListId = discoverChannelListId;
    }

    public String getDiscoverChannelName() {
        return discoverChannelName;
    }

    public void setDiscoverChannelName(String discoverChannelName) {
        this.discoverChannelName = discoverChannelName;
    }
}
